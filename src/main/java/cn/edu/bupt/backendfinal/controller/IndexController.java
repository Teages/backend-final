package cn.edu.bupt.backendfinal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Index", description = "样例控制器")
public class IndexController {
    @GetMapping("/")
    @Operation(description = "返回 Hello World")
    public String index() {
        return "Hello World";
    }
}
