package com.dsmc.config;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class SpringConfig {

  @Bean
  public ModelMapper modelMapper(List<PropertyMap> propertyMaps, List<AbstractConverter> converters) {
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setFieldMatchingEnabled(true)
//        mapper.getConfiguration()
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
}
