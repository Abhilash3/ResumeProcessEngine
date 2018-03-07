package com.epam.group;

import com.epam.group.request.UpdateGrouping;
import com.epam.group.vo.Grouping;
import com.mongodb.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.epam.common.Constants.Grouping.COLLECTION;
import static com.epam.common.Constants.Grouping.KEYWORDS;

@RestController
@RequestMapping(value = "/grouping")
class GroupingServices {

    private static final Logger logger = LoggerFactory.getLogger(GroupingServices.class);

    private static final String EMPTY_GROUPING_MSG = "Ignoring empty grouping";

    @Autowired
    private MongoTemplate template;

    @GetMapping(value = "/retrieve", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Grouping> retrieveGroupings() {
        logger.info("RetrieveGrouping{}");
        return template.findAll(Grouping.class, COLLECTION);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateGrouping(@RequestBody UpdateGrouping updateGrouping) {
        logger.info(updateGrouping.toString());

        WriteResult result = template.updateFirst(
                Query.query(keywordCriteria(updateGrouping.oldKeywords())),
                Update.update(KEYWORDS, updateGrouping.newKeywords()), Grouping.class, COLLECTION);

        logger.debug(result.toString());
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveGrouping(@RequestBody Grouping grouping) {
        if (grouping.keywords().size() == 0) {
            logger.info(EMPTY_GROUPING_MSG);
            return;
        }

        logger.info("Save" + grouping);
        template.insert(grouping, COLLECTION);
    }

    @DeleteMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteGrouping(@RequestBody Grouping grouping) {
        if (grouping.keywords().size() == 0) {
            logger.info(EMPTY_GROUPING_MSG);
            return;
        }
        logger.info("Remove" + grouping);

        Grouping removedObject = template.findAndRemove(
                Query.query(keywordCriteria(grouping.keywords())), Grouping.class, COLLECTION);

        logger.debug("Removed: " + removedObject);
    }

    private Criteria keywordCriteria(List<String> keywords) {
        Criteria criteria = Criteria.where(KEYWORDS).size(keywords.size());

        keywords.stream().map(keyword -> Criteria.where(KEYWORDS).in(keyword))
                .reduce((a, b) -> b.andOperator(a)).ifPresent(criteria::andOperator);

        return criteria;
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> handleExceptions(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(e.getMessage());
    }
}
