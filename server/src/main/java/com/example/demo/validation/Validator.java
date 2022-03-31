package com.example.demo.validation;

import com.example.demo.dto.CancelOrderRq;
import com.example.demo.dto.CreateOrderRq;
import com.example.demo.exception.ValidationException;
import org.springframework.stereotype.Component;


@Component
public class Validator {
    public void validate(CreateOrderRq rq) {
        Errors errors = new Errors();
        errors.checkNotNull("CreateOrderRq", rq, rqDto -> {
            errors.checkNotNull("companySymbol", rq.getCompanySymbol())
                    .checkNotNull("position", rq.getPosition())
                    .checkNotNull("amount", rq.getAmount(), amount -> {
                        errors.otherCheck("minAmount", () -> amount > 0);
                    })
                    .checkNotNull("price", rq.getPrice());
        });

        if (errors.isNotEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public void validate(CancelOrderRq rq) {
        Errors errors = new Errors();
        errors.checkNotNull("CancelOrderRq", rq, rqDto -> {
            errors.checkNotNull("companySymbol", rq.getCompanySymbol())
                    .checkNotNull("position", rq.getPosition())
                    .checkNotNull("amount", rq.getAmount(), amount -> {
                        errors.otherCheck("minAmount", () -> amount > 0);
                    })
                    .checkNotNull("price", rq.getPrice());
        });

        if (errors.isNotEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
