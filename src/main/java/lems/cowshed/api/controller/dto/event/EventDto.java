package lems.cowshed.api.controller.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.event.Category;
import lombok.Data;

import java.util.Date;

@Schema(description = "모임 상세")
@Data
public class EventDto {

    @Schema(description = "모임 이름", example = "농구 모임")
    String name;

    @Schema(description = "카테고리", example = "스포츠")
    Category category;

    @Schema(description = "등록일", example = "yyyy-mm-dd hh:mm:ss")
    Date createdDate;

    @Schema(description = "주소", example = "서울특별시 송파구 오금로55길")
    String address;

    @Schema(description = "장소 이름", example = "스카이 휘트니스")
    String location;

    @Schema(description = "내용", example = "같이 운동하실 분 구합니다. 같이 프레스 운동 하면서 서로 보조해주실 분 구합니다.")
    String content;

    @Schema(description = "모임 시간", example = "2024-09-12")
    Date eventDate;

    @Schema(description = "수용 인원", example = "100")
    int capacity;

    @Schema(description = "참여 신청 인원", example = "50")
    int applicants;

//    @Schema(description = "위도", example = "37.7")
//    double latitude;
//
//    @Schema(description = "경도", example = "126.9")
//    double longitude;

}