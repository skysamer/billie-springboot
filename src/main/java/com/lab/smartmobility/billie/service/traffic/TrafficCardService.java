package com.lab.smartmobility.billie.service.traffic;

import com.lab.smartmobility.billie.dto.traffic.TrafficCardForm;
import com.lab.smartmobility.billie.entity.*;
import com.lab.smartmobility.billie.repository.traffic.TrafficCardRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TrafficCardService {
    private final TrafficCardRepository cardRepository;
    private final ModelMapper modelMapper;
    private final Log log;

    /*보유 교통카드 목록 조회*/
    public List<TrafficCard> getPossessCardList(){
        try {
            return cardRepository.findAll();
        }catch (Exception e){
            return null;
        }
    }

    /*신규 교통카드 등록*/
    public int registerCard(TrafficCardForm trafficCardForm){
        try {
            TrafficCard trafficCard=new TrafficCard();
            modelMapper.map(trafficCardForm, trafficCard);
            cardRepository.save(trafficCard);
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
    }

    /*교통카드 개별 상세정보 조회*/
    public TrafficCard getCardInfo(Long cardNum){
        return cardRepository.findByCardNum(cardNum);
    }

    /*교통카드 등록 정보 수정*/
    public int updateCardInfo(TrafficCardForm trafficCardForm){
        try{
            TrafficCard updatedCardInfo=cardRepository.findByCardNum(trafficCardForm.getCardNum());
            modelMapper.map(trafficCardForm, updatedCardInfo);
            cardRepository.save(updatedCardInfo);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    /*교통카드 폐기*/
    public int discardCard(Long cardNum, HashMap<String, String> reason){
        TrafficCard trafficCard=cardRepository.findByCardNum(cardNum);
        if(trafficCard.getRentalStatus()==99){
            return 500;
        }

        trafficCard.discard(99, reason.get("reason"));
        cardRepository.save(trafficCard);
        return 0;
    }

    /*교통카드 정보 삭제*/
    public int removeCardInfo(Long cardNum){
        try{
            cardRepository.deleteByCardNum(cardNum);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

}
