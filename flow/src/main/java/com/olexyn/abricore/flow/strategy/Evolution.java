package com.olexyn.abricore.flow.strategy;

import com.olexyn.abricore.flow.strategy.templates.StrategyTemplates;
import com.olexyn.abricore.flow.tasks.VectorMergeTask;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.store.dao.EventDao;
import com.olexyn.abricore.store.runtime.SeriesService;
import com.olexyn.abricore.store.runtime.VectorService;
import com.olexyn.abricore.util.CtxAware;
import com.olexyn.abricore.util.log.LogU;
import com.olexyn.propconf.PropConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.olexyn.abricore.model.runtime.EventKeys.EVOLUTION_CURRENT_GENERATION;
import static com.olexyn.abricore.util.num.Num.ONE;
import static com.olexyn.abricore.util.num.Num.P10;
import static com.olexyn.abricore.util.num.Num.P20;
import static com.olexyn.abricore.util.num.Num.P30;
import static com.olexyn.abricore.util.num.Num.P50;
import static com.olexyn.abricore.util.num.Num.P70;
import static com.olexyn.abricore.util.num.Num.P80;
import static com.olexyn.abricore.util.num.Num.TWO;


@Component
public class Evolution extends CtxAware implements Runnable {

    private final Supplier<StrategyBuilder> sB = () -> bean(StrategyBuilder.class);
    private static final int POPULATION_SIZE = Integer.parseInt(PropConf.get("population.size"));
    private final Map<Integer, List<StrategyDto>> POPULATIONS = new TreeMap<>();
    private int currentGeneration = 0;
    private static final int KIDS_N = 4;
    private static final int MAX_GENERATIONS = Integer.parseInt(PropConf.get("max.generations"));
    private static final long[] IMPULSES = {P10, P20, P30, P50, P70, P80, ONE, TWO};
    private final Instant TIMER_START = Instant.now();

    @Autowired
    public Evolution(ConfigurableApplicationContext ctx) {
        super(ctx);
    }

    /**
     * Generate initial population.
     */
    public List<StrategyDto> makeInitialPopulation() {
        List<StrategyDto> population = new ArrayList<>();

        var strategy = bean(StrategyTemplates.class).evolutionTest();
        strategy.setFitness(0);
        populateSeriesFromDb(strategy);

        var vectors = new ArrayList<>(bean(VectorService.class).getVectors());

        // top by : rating * duratino * count
        vectors.sort((v1, v2) -> Long.compare(v2.getRating(), v1.getRating()));
        vectors.stream().limit(POPULATION_SIZE)
            .forEach(v -> population.add(strategy.cloneTemplate(v)));

        // if there are not enough vectors, use the strategy template
        while (population.size() < POPULATION_SIZE) {
            population.add(strategy.cloneTemplate());
        }

        lazyCalcFitness(population);
        MetricsCalculator.calcRating(population);
        population.sort(StrategyDto::compareTo);
        return population;
    }

    public void populateSeriesFromDb(StrategyDto strategy) {
        bean(SeriesService.class).of(
            strategy.getUnderlying(),
            strategy.getFrom(),
            strategy.getTo()
        );
    }

    public List<StrategyDto> evolve(List<StrategyDto> oldGen) {
        oldGen = new ArrayList<>(oldGen);
        oldGen.sort(StrategyDto::compareTo);
        List<StrategyDto> seed = makeSeed(oldGen, 5);

        List<StrategyDto> nextGen = new ArrayList<>(evolveFrom(seed));

        lazyCalcFitness(nextGen);
        nextGen = copyOfUniques(nextGen);
        nextGen.sort(StrategyDto::compareTo);

        int veteranCount = 0;
        while (veteranCount < POPULATION_SIZE / 20 && nextGen.size() < POPULATION_SIZE) {
            var veteran = oldGen.get(veteranCount++);
            nextGen.add(veteran);
        }

        var badSeed = makeSeed(oldGen, 2);
        var badKids = evolveFrom(badSeed);
        var badCount = 0;
        while (badCount < badKids.size() && nextGen.size() < POPULATION_SIZE) {
            nextGen.add(badKids.get(badCount++));
        }
        lazyCalcFitness(nextGen);
        nextGen.sort(StrategyDto::compareTo);
        nextGen = nextGen.stream().limit(POPULATION_SIZE).toList();
        return nextGen;
    }

    List<StrategyDto> makeSeed(List<StrategyDto> oldGen, int ratio) {
        List<StrategyDto> seed = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE / ratio && i < oldGen.size(); i++) {
            seed.add(oldGen.get(i));
        }
        return seed;
    }


    List<StrategyDto> evolveFrom(List<StrategyDto> seed) {
        List<StrategyDto> result = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE / 10; i++) {
            var parent1 = pickFromPool(seed);
            var parent2 = pickFromPool(seed);
            var child = sB.get().init(parent1).cloneTemplate().combine(parent2).build();
            for (var impulse : IMPULSES) {
                var mutant = sB.get().init(child).cloneTemplate().mutate(impulse).build();
                result.add(mutant);
            }
        }
        return result;
    }





    private void lazyCalcFitness(List<StrategyDto> strategies) {
        var fitnessMissing = strategies.stream().filter(s -> s.getFitness() == 0).toList();
        bean(MetricsCalculator.class).calculateFitness(fitnessMissing);
    }

    /**
     * Prevent population from being dominated by one solution.
     */
    private List<StrategyDto> copyOfUniques(List<StrategyDto> input) {
        Map<String, StrategyDto> uniqueWithTradesMap = new TreeMap<>();
        for (var strategy : input) {
            uniqueWithTradesMap.put(
                "KEY " + strategy.getFitness() + ' ' + strategy.getTrades().size(),
                strategy
            );
        }
        return new ArrayList<>(uniqueWithTradesMap.values());
    }

    StrategyDto pickFromPool(List<StrategyDto> pool) {
        double d = Math.random() * pool.size();
        String dStr = Double.toString(d);
        int i = Integer.parseInt(dStr.split("\\.")[0]);
        return pool.get(i);
    }

    @Override
    public void run() {
        POPULATIONS.put(currentGeneration, makeInitialPopulation());

        while (currentGeneration < MAX_GENERATIONS) {
            List<StrategyDto> newGen = evolve(POPULATIONS.get(currentGeneration));
            POPULATIONS.put(++currentGeneration, newGen);
            MetricsCalculator.calcRating(newGen);
            var newVectors = newGen.stream()
                .map(StrategyDto::getVector)
                .filter(v -> v.getRating() > 0)
                .collect(Collectors.toSet());
            bean(VectorService.class).addAll(newVectors);
            bean(VectorMergeTask.class).run();
            bean(EventDao.class).set(EVOLUTION_CURRENT_GENERATION, currentGeneration);
        }
        LogU.warnEnd("DONE");
    }

}
