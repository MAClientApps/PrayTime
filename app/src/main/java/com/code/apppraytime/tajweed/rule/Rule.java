package com.code.apppraytime.tajweed.rule;

import com.code.apppraytime.tajweed.model.Result;
import java.util.List;

public interface Rule {
  List<Result> checkAyah(String ayah);
}
