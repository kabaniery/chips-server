package com.kabaniery.chipsserver;


import com.kabaniery.chipsserver.Rooms.RoomManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainServers {
    public record ServerList(int status, String content) {}

    @GetMapping("/getrooms")
    public ServerList serverList(@RequestParam(value = "name", defaultValue = "none") String name) {
        RoomManager.defaultsRooms();
        return new ServerList(1, RoomManager.getRooms());
    }

}
