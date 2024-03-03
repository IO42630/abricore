package com.olexyn.abricore.store.csv;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.abricore.util.FileUtil;
import com.olexyn.abricore.util.enums.Interval;
import com.olexyn.abricore.util.exception.StoreException;
import com.olexyn.propconf.PropConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;

import static com.olexyn.abricore.util.Constants.COL;
import static com.olexyn.abricore.util.Constants.COMMA;
import static com.olexyn.abricore.util.Constants.CSV;
import static com.olexyn.abricore.util.Constants.EMPTY;
import static com.olexyn.abricore.util.Constants.SPACE;
import static com.olexyn.abricore.util.Constants.UL;

@Component
public class FileNameUtil {

    private final AssetService assetService;


    @Autowired
    private FileNameUtil(AssetService assetService) {
        this.assetService = assetService;
    }



    boolean containsAnyToken(Path filePath, Set<String> tokens) {
        boolean result = false;
        for (String token : tokens) {
            boolean tmpResult = containsToken(filePath, token);
            result = result || tmpResult;
        }
        return result;
    }

    boolean containsToken(Path filePath, String token) {
        String fileName = filePath.getFileName().toString();
        fileName = fileName.replace(CSV, EMPTY);
        String[] words = fileName.split("[_, -]");
        for (String word : words) {
            if (word.equalsIgnoreCase(token.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    AssetDto mapToFirstAsset(Path path) throws StoreException {
        return assetService.getNames().stream()
            .filter(str -> containsToken(path, str))
            .map(String::toUpperCase)
            .map(assetService::ofName)
            .filter(Objects::nonNull)
            .findFirst().orElseThrow(StoreException::new);
    }

    Interval mapToFirstInterval(Path path) throws StoreException {
        return Interval.getFileTokens().stream()
            .filter(str -> containsToken(path, str))
            .map(str -> Interval.ofFileToken(str.toUpperCase()))
            .findFirst().orElseThrow(StoreException::new);
    }

    Path getProcessedPath(Path path, AssetDto asset) {
        String fileName = (asset.getName() + "-SHA-" + FileUtil.getHash(path) + CSV)
            .replace(COL, UL)
            .replace(COMMA, UL)
            .replace(SPACE, UL);
        return Paths.get(PropConf.get("quotes.dir.processed"), fileName);
    }

}
