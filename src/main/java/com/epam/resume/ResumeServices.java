package com.epam.resume;

import com.epam.common.Constants;
import com.epam.common.Utils;
import com.epam.grouping.vo.Grouping;
import com.epam.resume.request.SearchResume;
import com.epam.resume.request.UpdateResume;
import com.epam.resume.vo.Resume;
import com.mongodb.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.epam.common.Constants.Resume.*;

@RestController
@RequestMapping(value = "/resume")
public class ResumeServices {

    private static final Logger logger = LoggerFactory.getLogger(ResumeServices.class);
    private static final ProjectionOperation RESUME_AND_EXPERIENCE =
            Aggregation.project(ID, EMAIL, GRADUATION, PROPERTIES, WORDS, NOTES).and(
                    ConditionalOperators.Cond.when(Criteria.where(GRADUATION).is(0)).then(-1).otherwiseValueOf(
                            ArithmeticOperators.Subtract.valueOf(Utils.currentYear()).subtract(GRADUATION))
            ).as(Constants.EXPERIENCE);
    private static final Sort SORT_BY_ID_ASC = new Sort(Sort.Direction.ASC, ID);
    private static final long ELEMENTS_SIZE = 20L;
    @Value("${application.resume.location}")
    private String basePath;
    @Autowired
    private MongoTemplate template;
    @Autowired
    private FileSystemResourceLoader resourceLoader;

    @GetMapping(value = "/open")
    public ResponseEntity<InputStreamResource> openResume(@RequestParam("id") String id) throws IOException {
        logger.info("OpenResume{id='{}'}", id);

        Resume resume = template.findOne(new Query(Criteria.where(ID).is(id)), Resume.class, COLLECTION);
        Resource resource = resourceLoader.getResource(basePath + resume.filePath());

        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header("Content-Disposition", "inline; filename=\"" + resume.fullName() + "\"")
                .contentType(MediaType.parseMediaType("application/" + resume.extension()))
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Resume> searchResume(@RequestBody SearchResume searchResume) {
        logger.info("{}", searchResume);

        Criteria ct = new Criteria().orOperator(
                Criteria.where(Constants.EXPERIENCE).is(-1),
                Criteria.where(Constants.EXPERIENCE).gte(searchResume.experience()));

        List<Grouping> allGroupings = template.findAll(Grouping.class, Constants.Grouping.COLLECTION);

        List<String> fields = searchResume.keywords().stream()
                .flatMap(keyword -> {
                    List<Grouping> groupings = allGroupings.stream()
                            .filter(a -> a.keywords().contains(keyword))
                            .collect(Collectors.toList());
                    if (!groupings.isEmpty()) {
                        return groupings.stream().flatMap(g -> g.keywords().stream());
                    } else {
                        return Stream.of(keyword);
                    }
                })
                .distinct()
                .map(s -> WORDS + Constants.PERIOD + s)
                .collect(Collectors.toList());

        ArithmeticOperators.Add relevance = ArithmeticOperators.Add.valueOf(0);
        for (String field : fields) {
            relevance = relevance.add(ConditionalOperators.IfNull.ifNull(field).then(0));
        }

        Aggregation pipeline = Aggregation.newAggregation(
                RESUME_AND_EXPERIENCE.and(relevance).as(Constants.RELEVANCE),
                Aggregation.match(ct),
                Aggregation.sort(new Sort(Sort.Direction.DESC, searchResume.sort()).and(SORT_BY_ID_ASC)),
                Aggregation.skip(searchResume.page() * ELEMENTS_SIZE),
                Aggregation.limit(ELEMENTS_SIZE));

        return template.aggregate(pipeline, COLLECTION, Resume.class).getMappedResults();
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateResume(@RequestBody UpdateResume updateResume) {
        logger.info("{}", updateResume);

        WriteResult result = template.updateFirst(
                Query.query(Criteria.where(ID).is(updateResume.id())),
                Update.update(updateResume.field(), updateResume.content()), COLLECTION);

        logger.debug("{}", result);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> handleExceptions(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(e.getMessage());
    }

    @Configuration
    protected class ResumeConfiguration {
        @Bean
        public FileSystemResourceLoader loader() {
            return new FileSystemResourceLoader();
        }
    }
}
