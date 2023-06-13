package org.cgi.homeconnect;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.stream.Stream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import org.cgi.RoastMarketCommerceModule;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@Path("/api/events")
public class HomeConnectAdapter {

  Credentials HOME_CONNECT_TOKEN = null;
  static String HC_BASE_URL = "https://simulator.home-connect.com";

  @Context
  Sse sse;
  SseBroadcaster broadcaster;

  HomeConnectAdapter(Sse sse) throws IOException, InterruptedException {
    this.sse = sse;
    this.broadcaster = sse.newBroadcaster();
  }

  @GET
  @Path("authorized")
  public Credentials isClientAuthorized() throws IOException, InterruptedException {
    if (HOME_CONNECT_TOKEN == null) {
      // TODO Handle empty credentials correctly
      return Credentials.createEmptyCredential();
    }

    if (HOME_CONNECT_TOKEN.hasExpired()) {
      HOME_CONNECT_TOKEN = HOME_CONNECT_TOKEN.refreshAccessToken();
    }

    return HOME_CONNECT_TOKEN;
  }

  @POST
  @Path("credentials")
  public void authCredentialsFromClient(Credentials credentials) {
    System.out.println("CREDENTIALS:");
    HOME_CONNECT_TOKEN = credentials;
    System.out.println(credentials);
  }

  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  public void subscribeToDeviceEvents() throws IOException, InterruptedException {
    System.out.println("Subscribing...");
    URI uri = URI.create(HC_BASE_URL + "/api/homeappliances/events");

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(uri)
        .headers("Accept", MediaType.SERVER_SENT_EVENTS, "Authorization", HOME_CONNECT_TOKEN.getAccess_token())
        .GET()
        .build();

    Stream<String> response = client.send(request, BodyHandlers.ofLines()).body();

    response.forEach(r -> {
      Stream<String> lines = r.lines();
      lines.forEach(l -> {
        int isObj = l.indexOf('{');
        if (l.startsWith("data:") && isObj > -1) {
          String raw = l.substring(isObj);
          JsonObject jo = new JsonObject(raw);
          JsonArray items = (JsonArray) jo.getValue("items");
          String value = (String) items.getJsonObject(0).getValue("value");
          System.out.println(value);

          if (value.equals("ConsumerProducts.CoffeeMaker.Program.Beverage.Espresso")) {

            OutboundSseEvent sseEvent = sse.newEventBuilder()
                .name("\tevent")
                .data(String.class, "\tpublished at " + LocalDate.now())
                .build();

            broadcaster.broadcast(sseEvent);

            try {
              RoastMarketCommerceModule.mockPurchaseFromShop();
            } catch (InterruptedException | ParseException e) {
              e.printStackTrace();
            }
          }
        }
      });
    });
  }

  @GET
  @Path("subscribe")
  @Produces(MediaType.SERVER_SENT_EVENTS)
  public void listen(@Context SseEventSink sseEventSink) throws InterruptedException {
    broadcaster.register(sseEventSink);
  }

  @GET
  @Path("publish")
  public void broadcast() {
    OutboundSseEvent sseEvent = sse.newEventBuilder()
        .name("\tevent")
        .data(String.class, "\tpublished at " + LocalDate.now())
        .build();

    broadcaster.broadcast(sseEvent);
  }

}
