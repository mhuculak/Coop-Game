package coop.engine;

import coop.map.*;
import coop.player.*;
import coop.action.*;
import coop.item.*;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.codehaus.jackson.map.ObjectMapper;

import javax.annotation.PostConstruct;
import java.util.*;

@Controller
public class WebSocketController {

    private static ApplicationContext context;    
    private static GamePlayers gamePlayers;
    private static GamePlaces gamePlaces;
    private static GameItems gameItems;
    private static GameWalls gameWalls;
    private static GameDoors gameDoors;

    private static int uniqueID = 0;

    public static int getUniqueID() {
        ++uniqueID;
        return uniqueID;
    }

    public static Player getPlayer(String playerID) {
        Player player = null;
        if (gamePlayers != null) {
            player = gamePlayers.findPlayer(playerID);
        }
        return player;
    }

    public static GameWalls getGameWalls() {
        return gameWalls;
    }

    public static Wall getWall(String name) {
        Wall wall = null;
        if (gameWalls != null) {
            wall = gameWalls.findWall(name);
        }
        return wall;
    }

    public static Wall getWallbyID(String wallID) {
        Wall wall = null;
        if (gameWalls != null) {
            wall = gameWalls.findWallbyID(wallID);
        }
        return wall;
    }

    public static Door getDoor(String doorID) {
        Door door = null;
        if (gameDoors != null) {
            door = gameDoors.findDoor(doorID);
        }
        return door;
    }

    public static Door getDoorBean(String doorID) {
        Door door = null;
        if (gameDoors != null) {
            door = (Door)context.getBean(doorID);
        }
        return door;
    }



    public static Place getPlace(Position position) {
        Place place = null;
        
        if (gamePlaces != null) {
            place = gamePlaces.findPlace(position);
        }
        return place;       
    }

    public static Place getPlace(int cellID) {
        Place place = null;
        
        if (gamePlaces != null) {
            place = gamePlaces.findPlace(cellID);
        }
        return place;       
    }

    public static Item getItem(String itemID) {
        Item item = null;
        if (gameItems != null) {
            item = gameItems.findItem(itemID);
        }
        return item;
    }

    public static void addItem(Item item) {        
        if (gameItems != null) {
            gameItems.addItem(item);
        }
    }

    public static void removeItem(Item item) {        
        if (gameItems != null) {
            gameItems.removeItem(item);
        }
    }

    @PostConstruct
    public void init() {
        context = new ClassPathXmlApplicationContext("Beans.xml");
        gameWalls = (GameWalls)context.getBean("walls");
        gameDoors = (GameDoors)context.getBean("doors");
        gamePlaces = (GamePlaces)context.getBean("places");
        gameItems = (GameItems)context.getBean("items");
        gamePlayers = (GamePlayers)context.getBean("players");
        gamePlayers.init();
        gameWalls.printWalls();
    }

    @MessageMapping("/quit" )   
    public void quitGame() throws Exception {
        System.out.println("quit game");
        ((ConfigurableApplicationContext)context).refresh();       
    }

    @MessageMapping("/chat" )
    @SendTo("/topic/showMessage")
    public ChatMessage receiveMsg(ChatMessage message) throws Exception {
        System.out.println("receiveMsg " + message.getMsg());        
        return message;
    }

    @MessageMapping("/dest" )
    @SendTo("/topic/dest")
    public Position receiveDest(PlayerPosition destPos) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(destPos);
        System.out.println("receiveDest: " + json);      
        Position newDest = gamePlayers.setTrajectory(destPos);       
        json = mapper.writeValueAsString(newDest);
        System.out.println("New Dest: " + json);
        return newDest;
    }

    @MessageMapping("/move" )
    @SendTo("/topic/showOptions")
    public PlayerOptions receiveMove(PlayerPosition playerPos) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(playerPos);
        System.out.println("receiveMove: " + json);      
        return gamePlayers.updatePosition(playerPos);       
    }

    @MessageMapping("/action" )
    @SendTo("/topic/showOptions")
    public PlayerOptions receiveAction(PlayerAction action) throws Exception {      
        PAction pact = action.getAction();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(pact);
        System.out.println(json);
        PlayerOptions po = gamePlayers.invokeAction(action);        
        json = mapper.writeValueAsString(po);
        System.out.println(json);
        return po;             
   }

   @SendTo("/topic/dest")
   public static Position sendDest(Position newDest) {
        return newDest;
   }

    @RequestMapping("/start")
    @SendTo("/topic/gameInfo")
    public GameInfo start() {
        System.out.println("Start new game");       
        return new GameInfo();
    }
    
} 
