package nl.ordina.raboservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("//payments")
public class PaymentController {

    @GetMapping("/test")
    public String greeting() {
        return "OK";
    }
}
