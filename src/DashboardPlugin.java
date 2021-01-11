package example;

// Mindustry Imports
import arc.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.net.Administration.*;
import mindustry.world.blocks.storage.*;

// Request Imports
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.image.DataBufferInt;
import java.io.IOException;

public class DashboardPlugin extends Plugin {

  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
  private static final String API_URL = "https://api.metamug.com";
  private static final String TOKEN = "sfsagdasdfsd";
  private static final String ADDRESS = "";
  private static OkHttpClient client = new OkHttpClient();

  //called when game initializes
  @Override
  public void init(){
    Events.on(PlayerJoin.class, event -> {
      updatePlayers();
    });
    Events.on(PlayerLeave.class, event -> {
      updatePlayers();
    });
    
    Events.on(MapMakeEvent);
    Events.on(MapPublishEvent);

  }

  //register commands that run on the server
  @Override
  public void registerServerCommands(CommandHandler handler){
    handler.register("update-dashboard", "Sends all server data to online dashboard.", args -> {
      EntityGroup<Player> players = Groups.player;
      struct.Seq<Map> maps = maps.all();
    });
  }

  public static String post(String json) throws IOException {
    RequestBody body = RequestBody.create(json, JSON);
    Request request = new Request.Builder()
      .url(API_URL)
      .post(body)
      .build();
    try (Response response = client.newCall(request).execute()) {
      return response.body().string();
    }
  }

  public static String requestJSON(String type, String data) {
    String json = String.format("{ \"request_type\": \"%s\", \"token\": \"%s\", \"address\": \"%s\", \"data\": %s }", type, TOKEN, ADDRESS, data);
    return json;
  }

  public static String updateMap(Strin name, String author) throws IOException {
    String data = String.format("{ \"name\": \"%s\", \"author\": \"%s\" }", name, author);
    String json = requestJSON("map-update", data);
   return  post(json);
  }
  public static String newChatMessage(String author, String content) throws IOException {
    String data = String.format("{ \"author\": \"%s\", \"content\": \"%s\" }", author, content);
    String json = requestJSON("new-chat-message", data);
    return post(json);
  }
  public static void updatePlayers() throws IOException {
    updatePlayers(Groups.players);
  }
  public static String updatePlayers(Player[] players) throws IOException {
    String data = "[ ";
    players.forEach((p) -> {
      data += ( "{ \"username\": \"" + p.name + "\", \"color\": \"" + p.color + "\" }, " );
    });
    data = data.substring(0, data.indexOf(","));
    data += " ]";
    String json = requestJSON("player-list-update", data);
    return post(json);
  }
}

