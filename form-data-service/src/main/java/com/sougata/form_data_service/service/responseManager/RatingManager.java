package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.RatingResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.RatingResDto;
import com.sougata.form_data_service.dto.response.question.RatingResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.RatingResponseSummaryDto;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.Rating;
import com.sougata.form_data_service.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service("RATING_RESPONSE_MANAGER")
public class RatingManager extends ResponseManager<RatingResponseAddReqDto, RatingResponseSummaryDto, RatingResDto, RatingResponseQuestionDto> {

    private final RatingRepository ratingRepository;

    @Autowired
    public RatingManager(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public void create(RatingResponseAddReqDto response, FormResponse formResponse) {
        Rating rating = new Rating();
        rating.setRating(response.getRating());
        rating.setQuestionId(response.getQuestionId());
        rating.setFormResponse(formResponse);

        ratingRepository.save(rating);
    }

    @Override
    public List<RatingResponseSummaryDto> getResponseSummaries(UUID formId, List<RatingResDto> questionResponses) {
        var responseSummaries = ratingRepository.getResponseSummaries(formId);
        var result = new ArrayList<RatingResponseSummaryDto>();

        var responseOptionCountMap = ratingRepository.getResponseRatingCount(formId)
                .stream().collect(Collectors.groupingBy(e -> e.get("questionId", Long.class)));

        questionResponses.forEach(qr ->
                result.add(
                        responseSummaries.stream()
                                .filter(rs -> Objects.equals(rs.questionId(), qr.getId()))
                                .map(rs -> {
                                    var r = new RatingResponseSummaryDto();

                                    r.setQuestionId(qr.getId());
                                    r.setQuestion(qr.getQuestion());
                                    r.setOrderIndex(qr.getOrderIndex());
                                    r.setNumberOfResponses(rs.numberOfResponses());
                                    r.setQuestionType(QuestionType.RATING);
                                    r.setRatingIcon(qr.getRatingIcon());
                                    r.setMaxRatingNumber(qr.getMaxRatingNumber());

                                    var ratingSum = 0L;
                                    var countMap = new HashMap<Integer, Long>();

                                    for (var cm : responseOptionCountMap.get(qr.getId())) {
                                        var rating = cm.get("rating", Integer.class);
                                        ratingSum += rating;
                                        countMap.put(rating, cm.get("responseCount", Long.class));
                                    }

                                    r.setAverageRating((double) (ratingSum / rs.numberOfResponses()));

                                    var scales = IntStream.rangeClosed(1, qr.getMaxRatingNumber()).boxed();

                                    var responses = scales.map(sc ->
                                            new RatingResponseSummaryDto.Response(
                                                    sc,
                                                    countMap.getOrDefault(sc, 0L)

                                            )).toList();

                                    r.setResponses(responses);

                                    return r;
                                })
                                .findFirst()
                                .orElseGet(() -> {
                                    var r = new RatingResponseSummaryDto();

                                    r.setQuestionId(qr.getId());
                                    r.setQuestion(qr.getQuestion());
                                    r.setOrderIndex(qr.getOrderIndex());
                                    r.setNumberOfResponses(0L);
                                    r.setQuestionType(QuestionType.RATING);
                                    r.setRatingIcon(qr.getRatingIcon());
                                    r.setMaxRatingNumber(qr.getMaxRatingNumber());
                                    r.setAverageRating(0d);
                                    r.setResponses(List.of());

                                    return r;
                                })
                )
        );

        return result;
    }

    @Override
    public RatingResponseQuestionDto getResponseByQuestion(UUID formId, RatingResDto questionRes) {
        var grouped = ratingRepository.groupedByRating(formId, questionRes.getId());

        var totalResponseCount = grouped.stream()
                .mapToLong(g -> g.get("responseCount", Long.class).intValue())
                .sum();

        var r = new RatingResponseQuestionDto();

        var responses = grouped.stream().map(g -> new RatingResponseQuestionDto.Response(
                g.get("rating", Integer.class),
                g.get("responseCount", Long.class).intValue(),
                Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList()
        )).toList();

        r.setQuestionId(questionRes.getId());
        r.setQuestion(questionRes.getQuestion());
        r.setQuestionType(questionRes.getQuestionType());
        r.setResponses(responses);
        r.setTotalResponseCount(totalResponseCount);

        return r;
    }


    @Override
    public QuestionType getQuestionType() {
        return QuestionType.RATING;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        var entities = ratingRepository.findByFormIdAndQuestionId(formId, questionId);

        ratingRepository.deleteAll(entities);
    }
}
