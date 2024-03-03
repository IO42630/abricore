package com.olexyn.abricore.store;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.olexyn.abricore.model.runtime.strategy.functions.DistanceGenerator;
import com.olexyn.abricore.model.runtime.strategy.functions.SizingCondition;
import com.olexyn.abricore.model.runtime.strategy.functions.TransactionCondition;
import com.olexyn.abricore.store.functions.condition.And;
import com.olexyn.abricore.store.functions.condition.HasBolTailSize;
import com.olexyn.abricore.store.functions.condition.HasFavorableSide;
import com.olexyn.abricore.store.functions.condition.HasRsiRadius;
import com.olexyn.abricore.store.functions.condition.HasTailDepth;
import com.olexyn.abricore.store.functions.condition.IgnoreFalse;
import com.olexyn.abricore.store.functions.condition.IgnoreTrue;
import com.olexyn.abricore.store.functions.condition.Or;
import com.olexyn.abricore.store.functions.condition.StopLossAtBol;
import com.olexyn.abricore.store.functions.condition.StopLossAtMa;
import com.olexyn.abricore.store.functions.generator.FactorDistance;
import com.olexyn.abricore.store.functions.generator.FixedSize;
import com.olexyn.abricore.util.exception.DataCorruptionException;
import com.olexyn.abricore.util.log.LogU;
import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.sql.rowset.serial.SerialClob;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.olexyn.abricore.util.Constants.CHARSET;

@UtilityClass
public class MapperHelper {

    private static final Kryo KRYO = new Kryo();

    static {
        // Common
        KRYO.register(long.class);
        KRYO.register(List.class);
        KRYO.register(ArrayList.class);
        // TransactionCondition
        KRYO.register(TransactionCondition.class);
        KRYO.register(And.class);
        KRYO.register(HasBolTailSize.class);
        KRYO.register(HasFavorableSide.class);
        KRYO.register(HasRsiRadius.class);
        KRYO.register(HasTailDepth.class);
        KRYO.register(IgnoreFalse.class);
        KRYO.register(IgnoreTrue.class);
        KRYO.register(Or.class);
        KRYO.register(StopLossAtBol.class);
        KRYO.register(StopLossAtMa.class);
        // DistanceGenerator
        KRYO.register(DistanceGenerator.class);
        KRYO.register(FactorDistance.class);
        // SizingCondition
        KRYO.register(SizingCondition.class);
        KRYO.register(FixedSize.class);
    }

    private static <T> byte[] toByteArray(T txc) {
        var baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        KRYO.writeClassAndObject(output, txc);
        output.close();
        return baos.toByteArray();
    }

    private static <T> @Nullable T fromByteArray(byte[] data, Class<T> clazz) {
        var bais = new ByteArrayInputStream(data);
        Input inputx = new Input(bais);
        Object obj = KRYO.readClassAndObject(inputx);
        inputx.close();
        if (obj == null) { return null; }
        return (T) obj;
    }

    public static <T> Clob toClob(T txc) {
        try {
            var byteBuffer = ByteBuffer.wrap(toByteArray(txc));
            var charBuffer = CHARSET.decode(byteBuffer);
            return new SerialClob(charBuffer.array());
        } catch (SQLException e) {
            LogU.infoPlain(e.getMessage());
            throw new DataCorruptionException(e.getMessage());
        }
    }

    public static <T> @Nullable T fromClob(Clob clob, Class<T> clazz) {
        var sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(clob.getCharacterStream())) {
            char[] buffer = new char[1024];
            int bytesRead = -1;
            while ((bytesRead = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, bytesRead);
            }
            var string = sb.toString();
            byte[] bytes = string.getBytes(CHARSET);
            return fromByteArray(bytes, clazz);
        } catch (SQLException | IOException e) {
            LogU.infoPlain(e.getMessage());
            throw new DataCorruptionException(e.getMessage());
        }
    }

    public static SerialClob toPrettyClob(String string) {
        try {
            return new SerialClob(string.toCharArray());
        } catch (SQLException e) {
            LogU.infoPlain(e.getMessage());
            throw new DataCorruptionException(e.getMessage());
        }
    }

    public static String fromPrettyClob(Clob clob) {
        var sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(clob.getCharacterStream())) {
            char[] buffer = new char[1024];
            int bytesRead = -1;
            while ((bytesRead = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, bytesRead);
            }
            return sb.toString();
        } catch (SQLException | IOException e) {
            LogU.infoPlain(e.getMessage());
            throw new DataCorruptionException(e.getMessage());
        }
    }

}
