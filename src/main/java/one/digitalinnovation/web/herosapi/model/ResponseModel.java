package one.digitalinnovation.web.herosapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseModel {

    private String message;
    private Heroes hero;

    public ResponseModel(String message) {
        this.message = message;
    }

    public ResponseModel(Heroes hero) {
        this.hero = hero;
    }

}
