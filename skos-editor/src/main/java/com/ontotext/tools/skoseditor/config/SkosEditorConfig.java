package com.ontotext.tools.skoseditor.config;

import com.ontotext.tools.skoseditor.repositories.ConceptsRepository;
import com.ontotext.tools.skoseditor.repositories.sesame.SesameConceptsRepository;
import com.ontotext.tools.skoseditor.services.ConceptsService;
import com.ontotext.tools.skoseditor.services.ExtractionService;
import com.ontotext.tools.skoseditor.services.impl.ConceptsServiceImpl;
import com.ontotext.tools.skoseditor.services.impl.ExtractionServiceImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.File;

@Configuration
@PropertySource("classpath:skos-editor.properties")
public class SkosEditorConfig {

    // TODO: fix this: does not read the prop file, the path is relative to the current work dir
    @Value("db.dir") String dbDir;

    @Bean(destroyMethod="shutDown")
    public Repository repository() {
        File dataDir = new File(dbDir);
        Repository repo = new SailRepository( new MemoryStore(dataDir) );
        try {
            repo.initialize();
        } catch (RepositoryException e) {
            throw new IllegalStateException("Failed to initialize repository.", e);
        }
        return repo;
    }


    @Bean
    public ExtractionService extractionService() {
        return new ExtractionServiceImpl();
    }

    @Bean
    public ConceptsRepository conceptsRepository() {
        return new SesameConceptsRepository(repository());
    }

    @Bean
    public ConceptsService conceptsService() {
        return new ConceptsServiceImpl(conceptsRepository());
    }

}
