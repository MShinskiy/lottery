package com.lavkatech.lottery.contoller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lavkatech.lottery.entity.User;
import com.lavkatech.lottery.entity.dto.DecodedUserQuery;
import com.lavkatech.lottery.entity.enumeration.Group;
import com.lavkatech.lottery.entity.enumeration.Level;
import com.lavkatech.lottery.service.db.UserService;
import com.lavkatech.lottery.util.CipherUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dev")
public class TestController {

    @Value("${cipher.initVector}")
    private String initVector;
    @Value("${cipher.key}")
    private String key;

    private final UserService userService;

    @Autowired
    public TestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<Object> createTestUsers(@RequestParam("vu") Integer nUsers) {

        // Сгенерировать пользователей
        Map<DecodedUserQuery, String> encodingsMap = new HashMap<>();
        Set<String> dtprfs = new HashSet<>(); // контроль дублей
        for(int i = 0; i < nUsers; i++) {
            // создать пользователя
            DecodedUserQuery userQuery = createRandomUser();
            // повтор при дубле
            if(dtprfs.contains(userQuery.dtprf())) {
                i--;
                continue;
            } else
                dtprfs.add(userQuery.dtprf());
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                // маршеллинг в json
                String json = mapper.writeValueAsString(userQuery);
                // шифрование в AES 256
                String encoded = CipherUtility.encrypt(json, key, initVector);
                // сохранение в карте
                encodingsMap.put(userQuery, encoded);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // Сохранить пользователей в БД
        List<User> users = encodingsMap.keySet().stream()
                // Дать билеты пользователям для теста
                .map(q -> {
                    User u = User.fromQuery(q);
                    u.setTickets(10000);
                    return u;
                }).toList();
        userService.saveUsers(users);

        // Передать дтпрф-шифр в ответе
        record EntryPair(String dtprf, String encoded) { } // локальный класс для передачи
        List<EntryPair> dtprfPairs = encodingsMap.entrySet().stream().map(e -> new EntryPair(e.getKey().dtprf(), e.getValue())).toList();
        return ResponseEntity.ok(dtprfPairs);
    }

    private DecodedUserQuery createRandomUser() {
        Random rnd = new Random();
        String dtrpf = "DTPRF" + rnd.nextInt(100000);
        Group group = rnd.nextInt(2) == 0 ? Group.PARTNER : Group.OTHER;
        Level level = rnd.nextInt(2) == 0 ? Level.LOW : Level.HIGH;

        return new DecodedUserQuery(dtrpf, group, level);
    }
}
