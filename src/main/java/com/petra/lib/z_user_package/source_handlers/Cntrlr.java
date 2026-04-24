package com.petra.lib.z_user_package.source_handlers;

import com.petra.lib.controller.PetraController;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class Cntrlr {

    PetraController petraController;

    @PostMapping("/start")
    ResponseEntity<String> get() {
        Collection<Long> fstCol = new ArrayList<>();
        fstCol.add(11L);
        fstCol.add(22L);

        Collection<String> scdCol = new ArrayList<>();
        scdCol.add("fst value");
        scdCol.add("scnd value");

        Map<String , Object> values = Map.of("fstIn", fstCol, "scdIn", scdCol);

        try {
            petraController.executeWorkflow("root","0", values);
        }catch (Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.ok().body("OK");
    }
}
