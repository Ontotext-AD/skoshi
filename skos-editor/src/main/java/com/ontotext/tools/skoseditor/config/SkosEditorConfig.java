package com.ontotext.tools.skoseditor.config;

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
@PropertySource(value="classpath:skos-editor.properties")
public class SkosEditorConfig {

    @Value("db.dir") String dbDir;

    @Bean
    public Repository getRepository() {
        File dataDir = new File(dbDir);
        Repository repo = new SailRepository( new MemoryStore(dataDir) );
        try {
            repo.initialize();
        } catch (RepositoryException e) {
            throw new IllegalStateException("Failed to initialize repository.", e);
        }
        return repo;
    }


}
