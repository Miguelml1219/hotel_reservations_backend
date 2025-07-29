package com.hotel.reservations.controller;


import com.hotel.reservations.model.Room;
import com.hotel.reservations.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/hotel/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room){
        Room newRoom = roomService.registerRoom(room);
        return ResponseEntity.ok(newRoom);
    }

    @GetMapping
    public ResponseEntity<List<Room>> obtainAll(){
        return ResponseEntity.ok(roomService.obtainAll());
    }

    @GetMapping("/availables")
    public ResponseEntity<List<Room>> obtainAvailables(){
        return ResponseEntity.ok(roomService.obtainAvailables());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Room>> filterByAvailables(
        @RequestParam String type,
        @RequestParam int capacity){

        return ResponseEntity.ok(roomService.filterByAvailables(type,capacity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> obtainById(@PathVariable String id){
        return roomService.obtainById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable String id, @RequestBody Room room){
        Room update = roomService.updateRoom(id, room);
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String id){
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableRoom(@PathVariable String id){
        roomService.markNotAvailable(id);
        return ResponseEntity.noContent().build();
    }

}
