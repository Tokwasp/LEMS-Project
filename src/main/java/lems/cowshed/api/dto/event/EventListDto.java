package lems.cowshed.api.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Date;

@Schema(description = "모임 목록")
@Getter
public class EventListDto {
    String name;
    String content;
    Date eventDate;
    int capacity;
    int applicants;
    Date createdDate;
    Long userId; //로그인 상태면 id를 파싱, 아니면 null
}