package http;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
//import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import java.io.IOException;
import java.util.LinkedList;

public class ChessSerializer {

    public static Gson gson() {
        return new Gson();
//        return getBuilder().create();
    }

    public static GsonBuilder getBuilder() {
//        LinkedList<RuntimeTypeAdapterFactory> factories = new LinkedList<>();

//        factories.push(RuntimeTypeAdapterFactory.of(ChessGame.class, "type").registerSubtype(ChessGameImpl.class));
//        factories.push(RuntimeTypeAdapterFactory.of(ChessBoard.class, "type").registerSubtype(ChessBoardImpl.class));
//        factories.push(RuntimeTypeAdapterFactory.of(ChessPiece.class, "pieceType")
//                .registerSubtype(King.class)
//                .registerSubtype(Queen.class)
//                .registerSubtype(Knight.class)
//                .registerSubtype(Rook.class)
//                .registerSubtype(Bishop.class)
//                .registerSubtype(Pawn.class));

        GsonBuilder builder = new GsonBuilder();
//        for (RuntimeTypeAdapterFactory factory : factories) {
//            builder.registerTypeAdapterFactory(factory);
//        }

        builder.registerTypeAdapter(ChessMove.class, getChessMoveAdapter());
        builder.registerTypeAdapter(ChessPosition.class, getChessPositionAdapter());
        return builder;
    }

    private static TypeAdapter<ChessMove> getChessMoveAdapter() {
        return new TypeAdapter<>() {
            @Override
            public void write(JsonWriter jsonWriter, ChessMove move) throws IOException {
                String json = new Gson().toJson(move);
                jsonWriter.value(json);
            }

            @Override
            public ChessMove read(JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() == JsonToken.STRING) {
                    String moveJson = jsonReader.nextString();
                    return getChessMoveAdapter().fromJson(moveJson);
                }

                jsonReader.beginObject();

                ChessPosition startPosition = readChessPositionField(jsonReader);
                ChessPosition endPosition = readChessPositionField(jsonReader);

                if (jsonReader.peek() == JsonToken.END_OBJECT) {
                    jsonReader.endObject();
                    return new ChessMove(startPosition, endPosition);
                } else {
                    ChessPiece.PieceType promotionPiece = readPieceTypeField(jsonReader);
                    jsonReader.endObject();
                    return new ChessMove(startPosition, endPosition, promotionPiece);
                }
            }

            private static ChessPosition readChessPositionField(JsonReader jsonReader) throws IOException {
                String positionFieldName = jsonReader.nextName();
                if (jsonReader.peek() == JsonToken.STRING) {
                    String posJson = jsonReader.nextString();
                    return getChessPositionAdapter().fromJson(posJson);
                } else {
                    return readChessPosition(jsonReader);
                }
            }

            private ChessPiece.PieceType readPieceTypeField(JsonReader jsonReader) throws IOException {
                jsonReader.nextName();
                String pieceTypeString = jsonReader.nextString();
                try {
                    return ChessPiece.PieceType.valueOf(pieceTypeString);
                } catch (IllegalArgumentException e) {
                    throw new JsonParseException("Failed to parse unrecognized PieceType: '" + pieceTypeString + "'");
                }
            }
        };
    }

    private static TypeAdapter<ChessPosition> getChessPositionAdapter() {
        return new TypeAdapter<>() {
            @Override
            public void write(JsonWriter jsonWriter, ChessPosition pos) throws IOException {
                String json = new Gson().toJson(pos);
                jsonWriter.value(json);
            }

            @Override
            public ChessPosition read(JsonReader jsonReader) throws IOException {
                return readChessPosition(jsonReader);
            }
        };
    }

    private static ChessPosition readChessPosition(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        String rowFieldName = jsonReader.nextName();
        int row = jsonReader.nextInt();
        String colFieldName = jsonReader.nextName();
        int col = jsonReader.nextInt();
        jsonReader.endObject();
        return new ChessPosition(row, col);
    }
}
