package com.dsmc.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

import reactor.Environment;
import reactor.bus.EventBus;

@Configuration
@SuppressWarnings("unused")
public class SpringConfig {

  @Autowired
  private MongoDbFactory mongoDbFactory;

  @Bean
  public ModelMapper modelMapper(List<PropertyMap> propertyMaps, List<AbstractConverter> converters) {
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setFieldMatchingEnabled(true)
        .setSourceNamingConvention(NamingConventions.JAVABEANS_ACCESSOR)
        .setDestinationNamingConvention(NamingConventions.JAVABEANS_MUTATOR)
        .setMatchingStrategy(MatchingStrategies.STRICT);
    propertyMaps.forEach(mapper::addMappings);
    converters.forEach(mapper::addConverter);
    return mapper;
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:9000");
      }
    };
  }

  @Bean
  @Primary
  public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
    return builder.serializationInclusion(JsonInclude.Include.NON_NULL) // Don’t include null values
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) //ISODate
        .modules(new JavaTimeModule())
        .createXmlMapper(false)
        .build();
  }

  @Bean
  Environment env() {
    return Environment.initializeIfEmpty()
        .assignErrorJournal();
  }

  @Bean
  EventBus createEventBus(Environment env) {
    return EventBus.create(env, Environment.THREAD_POOL);
  }

  @Bean
  public MongoTemplate mongoTemplate() throws Exception {
    return new MongoTemplate(mongoDbFactory, getDefaultMongoConverter());

  }

  @Bean
  MappingMongoConverter getDefaultMongoConverter() throws Exception {
    return new MappingMongoConverter(
        new DefaultDbRefResolver(mongoDbFactory), new MongoMappingContext());
  }
}
