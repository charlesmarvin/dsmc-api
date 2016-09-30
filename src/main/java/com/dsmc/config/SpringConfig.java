package com.dsmc.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

import reactor.Environment;
import reactor.bus.EventBus;

@Configuration
public class SpringConfig {

  @Autowired
  MongoDbFactory mongoDbFactory;

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
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
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
  public MappingMongoConverter getDefaultMongoConverter() throws Exception {
    return new MappingMongoConverter(
        new DefaultDbRefResolver(mongoDbFactory), new MongoMappingContext());
  }
}
