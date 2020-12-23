package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * <a href="https://adventofcode.com/2020/day/21">Day 21: Allergen Assessment</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day21 {

  @Value
  static class FoodList {

    List<Food> foods;

    static FoodList parse(List<String> lines) {
      return new FoodList(lines.stream().map(Food::parse).collect(Collectors.toList()));
    }

    FactMap calculateFactMap() {
      Map<Allergen, Optional<AllergenAndIngredients>> optionalAllergens = foods.stream()
          .flatMap(food -> food.getAllergenAndIngredients().stream())
          .collect(groupingBy(AllergenAndIngredients::getAllergen,
                              Collectors.reducing(AllergenAndIngredients::intersection)));
      Map<Allergen, List<Ingredient>> allergens = optionalAllergens.entrySet().stream()
          .filter(entry -> entry.getValue().isPresent())
          .collect(Collectors.toMap(Map.Entry::getKey, optionalIngredients -> optionalIngredients.getValue().get().getIngredients()));
      Map<Ingredient, Allergen> facts = new HashMap<>();
      while (!allergens.isEmpty()) {
        Optional<AllergenAndIngredients> allergenWithSingleIngredient = allergens.entrySet().stream()
            .filter(entry -> entry.getValue().size() == 1)
            .map(entry -> new AllergenAndIngredients(entry.getKey(), entry.getValue()))
            .findAny();
        if (allergenWithSingleIngredient.isPresent()) {
          Allergen allergen = allergenWithSingleIngredient.get().getAllergen();
          Ingredient ingredientToRemove = allergenWithSingleIngredient.get().getIngredients().get(0);
          facts.put(ingredientToRemove, allergen);
          allergens = allergens.entrySet().stream()
              .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                List<Ingredient> ingredients = new ArrayList<>(entry.getValue());
                ingredients.remove(ingredientToRemove);
                return ingredients;
              }));
          allergens = allergens.entrySet().stream()
              .filter(entry -> !entry.getValue().isEmpty())
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
      }
      return new FactMap(facts);
    }

    long countIngredientsWithNoAllergens(FactMap factMap) {
      return foods.stream()
          .flatMap(food -> food.getIngredients().stream())
          .filter(ingredient -> !factMap.containsIngredient(ingredient))
          .count();
    }

    @Override
    public String toString() {
      return foods.stream().map(Food::toString).collect(Collectors.joining("\n"));
    }

  }

  @Value
  static class FactMap {

    Map<Ingredient, Allergen> facts;

    String getCanonicalDangerousIngredientList() {
      return facts.entrySet().stream()
          .sorted(Comparator.comparing(entry -> entry.getValue().getName()))
          .map(entry -> entry.getKey().getName())
          .collect(Collectors.joining(","));
    }

    public boolean containsIngredient(Ingredient ingredient) {
      return facts.containsKey(ingredient);
    }
  }

  @Value
  static class AllergenAndIngredients {
    Allergen allergen;
    List<Ingredient> ingredients;

    public static AllergenAndIngredients intersection(AllergenAndIngredients src, AllergenAndIngredients dest) {
      List<Ingredient> retainedIngredients = new ArrayList<>(src.getIngredients());
      retainedIngredients.retainAll(dest.getIngredients());
      return new AllergenAndIngredients(src.getAllergen(), retainedIngredients);
    }
  }

  @Value
  static class Food {

    private static final Pattern LINE_PATTERN = Pattern.compile("([^(]+)\\b\\s+\\(contains\\s+\\b([^)]+)\\)");

    List<Ingredient> ingredients;
    List<Allergen> allergens;

    static Food parse(String listAsString) {
      Matcher matcher = LINE_PATTERN.matcher(listAsString);
      if (matcher.matches()) {
        return new Food(Ingredient.parseList(matcher.group(1)), Allergen.parseList(matcher.group(2)));
      }
      else {
        throw new IllegalArgumentException("Unrecognized food definition: " + listAsString);
      }
    }

    List<AllergenAndIngredients> getAllergenAndIngredients() {
      return allergens.stream().map(allergen -> new AllergenAndIngredients(allergen, ingredients)).collect(Collectors.toList());
    }

    @Override
    public String toString() {
      return String.format(
          "%s (contains %s)",
          ingredients.stream().map(Ingredient::toString).collect(Collectors.joining(" ")),
          allergens.stream().map(Allergen::toString).collect(Collectors.joining(", "))
      );
    }

  }

  @Value
  static class Ingredient {

    private static final Pattern LIST_SPLIT_PATTERN = Pattern.compile("\\s+");

    String name;

    static List<Ingredient> parseList(String listAsString) {
      return Arrays.stream(LIST_SPLIT_PATTERN.split(listAsString))
          .map(Ingredient::new).collect(Collectors.toList());
    }

    @Override
    public String toString() {
      return name;
    }

  }

  @Value
  static class Allergen {

    private static final Pattern LIST_SPLIT_PATTERN = Pattern.compile(",\\s+");

    String name;

    static List<Allergen> parseList(String listAsString) {
      return Arrays.stream(LIST_SPLIT_PATTERN.split(listAsString))
          .map(Allergen::new).collect(Collectors.toList());
    }

    @Override
    public String toString() {
      return name;
    }

  }

}
