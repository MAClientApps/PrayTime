package com.code.apppraytime.tajweed.model;

import com.code.apppraytime.tajweed.rule.GhunnaRule;
import com.code.apppraytime.tajweed.rule.IdghamRule;
import com.code.apppraytime.tajweed.rule.IkhfaRule;
import com.code.apppraytime.tajweed.rule.IqlabRule;
import com.code.apppraytime.tajweed.rule.MaadRule;
import com.code.apppraytime.tajweed.rule.MeemRule;
import com.code.apppraytime.tajweed.rule.QalqalahRule;
import com.code.apppraytime.tajweed.rule.Rule;

import java.util.Arrays;
import java.util.List;

public class TajweedRule {

  public static final List<TajweedRule> MADANI_RULES = Arrays.asList(
      new TajweedRule(new GhunnaRule()),
      new TajweedRule(new IdghamRule()),
      new TajweedRule(new IkhfaRule()),
      new TajweedRule(new IqlabRule()),
      new TajweedRule(new MeemRule()),
      new TajweedRule(new QalqalahRule()),
      new TajweedRule(new MaadRule())
  );

  public final Rule rule;

  public TajweedRule(Rule rule) {
    this.rule = rule;
  }
}
