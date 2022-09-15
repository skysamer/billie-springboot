package com.lab.smartmobility.billie.corporation.controller;

import com.lab.smartmobility.billie.corporation.dto.CorporationCardForm;
import com.lab.smartmobility.billie.corporation.dto.DisposalForm;
import com.lab.smartmobility.billie.corporation.domain.CorporationCard;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.corporation.service.CorporationCardService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/corporation-card/*")
@Api(tags = {"법인카드 api"})
@RequiredArgsConstructor
public class CorporationCardController {
    private final CorporationCardService service;

    @ApiOperation(value = "신규 법인카드 등록")
    @ApiResponses({
            @ApiResponse(code = 200, message = "fail-insert // success-insert")
    })
    @PostMapping("/insert")
    public HttpBodyMessage createNewCard(@RequestBody CorporationCardForm corporationCardForm){
        int isInserted = service.createCard(corporationCardForm);
        if(isInserted == 9999){
            return new HttpBodyMessage("fail", "fail-insert");
        }
        return new HttpBodyMessage("success", "success-insert");
    }

    @ApiOperation(value = "보유 법인카드 목록 조회")
    @ApiImplicitParam(name = "disposal-info", value = "폐기정보 포함:1, 미포함:0")
    @GetMapping("/list/{disposal-info}")
    public List<CorporationCard> getCardList(@PathVariable("disposal-info") int disposalInfo){
        return service.getCardList(disposalInfo);
    }

    @ApiOperation(value = "개별 법인카드 정보 상세 조회")
    @ApiImplicitParam(
            name = "card-id",
            value = "카드 고유 시퀀스"
    )
    @GetMapping("/{card-id}")
    public CorporationCard getCardInfo(@PathVariable("card-id") Long cardId){
        return service.getCardInfo(cardId);
    }

    @ApiOperation(value = "법인카드 정보 수정")
    @ApiImplicitParam(name = "card-id", value = "카드 고유 시퀀스")
    @ApiResponses({
            @ApiResponse(code = 200, message = "fail-modify // success-modify")
    })
    @PutMapping("/modify/{card-id}")
    public HttpBodyMessage modifyCardInfo(@PathVariable("card-id") Long cardId, @RequestBody CorporationCardForm corporationCardForm){
        int isModified = service.modifyCardInfo(cardId, corporationCardForm);
        if(isModified == 9999){
            return new HttpBodyMessage("fail", "fail-modify");
        }
        return new HttpBodyMessage("success", "success-modify");
    }

    @ApiOperation(value = "법인카드 폐기")
    @ApiImplicitParam(name = "card-id", value = "카드 고유 시퀀스")
    @ApiResponses({
            @ApiResponse(code = 200, message = "fail-discard // success-discard")
    })
    @PatchMapping("/disposal/{card-id}")
    public HttpBodyMessage abrogate(@PathVariable("card-id") Long cardId, @RequestBody DisposalForm disposalForm){
        int isDiscarded= service.abrogate(cardId, disposalForm);
        if(isDiscarded==9999){
            return new HttpBodyMessage("fail", "fail-discard");
        }
        return new HttpBodyMessage("success", "success-discard");
    }

    @ApiOperation(value = "법인카드 정보 삭제")
    @ApiImplicitParam(name = "card-id", value = "카드 고유 시퀀스")
    @ApiResponses({
            @ApiResponse(code = 200, message = "fail-remove // success-remove // not-exist-card-info")
    })
    @DeleteMapping("/remove/{card-id}")
    public HttpBodyMessage remove(@PathVariable("card-id") Long cardId){
        return service.remove(cardId);
    }
}
