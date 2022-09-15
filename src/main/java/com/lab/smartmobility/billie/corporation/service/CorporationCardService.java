package com.lab.smartmobility.billie.corporation.service;

import com.lab.smartmobility.billie.corporation.dto.CorporationCardForm;
import com.lab.smartmobility.billie.corporation.dto.DisposalForm;
import com.lab.smartmobility.billie.corporation.repository.CorporationCardRepository;
import com.lab.smartmobility.billie.corporation.repository.CorporationCardRepositoryImpl;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.corporation.domain.CorporationCard;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CorporationCardService {
    private final CorporationCardRepository cardRepository;
    private final CorporationCardRepositoryImpl cardRepositoryImpl;

    private final ModelMapper modelMapper;
    private final Log log;

    /*신규 법인카드 등록*/
    public int createCard(CorporationCardForm corporationCardForm){
        try{
            CorporationCard newCorporationCard=modelMapper.map(corporationCardForm, CorporationCard.class);
            cardRepository.save(newCorporationCard);
        }catch (Exception e){
            log.error("fail : "+e);
            return 9999;
        }
        return 0;
    }

    /*보유 법인카드 목록 조회*/
    public List<CorporationCard> getCardList(int disposalInfo){
        return cardRepositoryImpl.findAll(disposalInfo);
    }

    /*개별 법인카드 정보 상세 조회*/
    public CorporationCard getCardInfo(Long cardId){
        return cardRepository.findByCardId(cardId);
    }

    /*개별 법인카드 정보 수정*/
    public int modifyCardInfo(Long cardId, CorporationCardForm corporationCardForm){
        try{
            CorporationCard card=cardRepository.findByCardId(cardId);
            modelMapper.map(corporationCardForm, card);
            cardRepository.save(card);
        }catch (Exception e){
            log.error("fail : "+e);
            return 9999;
        }
        return 0;
    }

    /*법인카드 폐기*/
    public int abrogate(Long cardId, DisposalForm disposalForm){
        try{
            CorporationCard card=cardRepository.findByCardId(cardId);
            card.discard(99, disposalForm.getReasonForDisposal());
            cardRepository.save(card);
        }catch (Exception e){
            log.error("fail : "+e);
            return 9999;
        }
        return 0;
    }

    /*법인카드 정보 삭제*/
    public HttpBodyMessage remove(Long cardId){
        CorporationCard corporationCard=cardRepository.findByCardId(cardId);
        if(corporationCard==null){
            return new HttpBodyMessage("fail", "not-exist-card-info");
        }

        try{
            cardRepository.delete(corporationCard);
        }catch (Exception e){
            log.error(e);
            return new HttpBodyMessage("fail", "fail-remove-card");
        }
        return new HttpBodyMessage("success", "success-remove-card");
    }
}
