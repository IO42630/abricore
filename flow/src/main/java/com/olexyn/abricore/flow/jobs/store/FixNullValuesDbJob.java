package com.olexyn.abricore.flow.jobs.store;

import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.flow.jobs.Job;
import com.olexyn.abricore.model.data.PositionEntity;
import com.olexyn.abricore.model.data.SnapshotEntity;
import com.olexyn.abricore.model.data.SymbolEntity;
import com.olexyn.abricore.model.data.TradeEntity;
import com.olexyn.abricore.store.repo.PositionRepo;
import com.olexyn.abricore.store.repo.SnapshotRepo;
import com.olexyn.abricore.store.repo.SymbolRepo;
import com.olexyn.abricore.store.repo.TradeRepo;
import com.olexyn.abricore.util.enums.FlowHint;
import com.olexyn.abricore.util.log.LogU;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static com.olexyn.abricore.util.Constants.EMPTY;
import static com.olexyn.abricore.util.Constants.NULL_STR;
import static com.olexyn.abricore.util.enums.FlowHint.OK;

public class FixNullValuesDbJob extends Job {

    public FixNullValuesDbJob(ConfigurableApplicationContext ctx) {
        super(ctx);
    }

    private static final int PAGE_SIZE = 1000;

    @Override
    public void run() {

        LogU.infoStart(EMPTY);
        fixPositionRepo();
        fixSnapshotRepo();
        fixSymbolRepo();
        fixTradeRepo();
        LogU.infoEnd(EMPTY);
    }

    private String fixNull(String value) {
        return value == null ? NULL_STR : value;
    }

    @Override
    public FlowHint fetchData() {
        // No Data needs to be fetched for this Job.
        return OK;
    }

    @Override
    public JobType getType() { return JobType.FIX_NULL_VALUES_DB; }

    private void fixPositionRepo() {
        var repo = bean(PositionRepo.class);
        int pageNumber = 0;
        Page<PositionEntity> page;
        do {
            page = repo.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
            var content = page.getContent();
            content.forEach(e -> {
                e.setAmount(fixNull(e.getAmount()));
                e.setPrice(fixNull(e.getPrice()));
            });
            repo.saveAll(content);
            pageNumber++;
        } while (pageNumber < page.getTotalPages());
    }

    private void fixSnapshotRepo() {
        var repo = bean(SnapshotRepo.class);
        int pageNumber = 0;
        Page<SnapshotEntity> page;
        do {
            page = repo.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
            var content = page.getContent();
            content.forEach(e -> {
                e.setAskPrice(fixNull(e.getAskPrice()));
                e.setBidPrice(fixNull(e.getBidPrice()));
                e.setTradedPrice(fixNull(e.getTradedPrice()));
                e.setRange(fixNull(e.getRange()));
                e.setVolume(fixNull(e.getVolume()));
            });
            repo.saveAll(content);
            pageNumber++;
        } while (pageNumber < page.getTotalPages());
    }

    private void fixSymbolRepo() {
        var repo = bean(SymbolRepo.class);
        int pageNumber = 0;
        Page<SymbolEntity> page;
        do {
            page = repo.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
            var content = page.getContent();
            content.forEach(e -> {
                e.setRatio(fixNull(e.getRatio()));
                e.setStrike(fixNull(e.getStrike()));
            });
            repo.saveAll(content);
            pageNumber++;
        } while (pageNumber < page.getTotalPages());
    }

    private void fixTradeRepo() {
        var repo = bean(TradeRepo.class);
        int pageNumber = 0;
        Page<TradeEntity> page;
        do {
            page = repo.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
            var content = page.getContent();
            content.forEach(e -> {
                e.setAmount(fixNull(e.getAmount()));
                e.setBuyFee(fixNull(e.getBuyFee()));
                e.setBuyPrice(fixNull(e.getBuyPrice()));
                e.setSellFee(fixNull(e.getSellFee()));
                e.setSellPrice(fixNull(e.getSellPrice()));
            });
            repo.saveAll(content);
            pageNumber++;
        } while (pageNumber < page.getTotalPages());
    }

}
