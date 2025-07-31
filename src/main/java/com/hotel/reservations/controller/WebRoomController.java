package com.hotel.reservations.controller;


import org.springframework.ui.Model;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

@Controller
@RequestMapping("/hotel")
public class WebRoomController {

    private final RoomService roomService;

    public WebRoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/rooms")
    public String listRooms(Model model){
        List<Room> rooms = roomService.obtainAll();
        model.addAttribute("rooms", rooms);
        return "rooms";
    }

}
