package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.dto.corporation.CorporationCardForm;
import com.lab.smartmobility.billie.entity.CorporationCard;
import com.lab.smartmobility.billie.repository.corporation.CorporationCardRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CorporationCardService {
    private final CorporationCardRepository cardRepository;
    private final ModelMapper modelMapper;
    private final Log log;

    /*신규 법인카드 등록*/
    public void insertCard(CorporationCardForm corporationCardForm){
        cardRepository.save(modelMapper.map(corporationCardForm, CorporationCard.class));
    }
}
