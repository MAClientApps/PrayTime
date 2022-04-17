package com.code.apppraytime.tajweed.rule;

import com.code.apppraytime.tajweed.model.Result;
import com.code.apppraytime.tajweed.model.ResultType;
import com.code.apppraytime.tajweed.util.CharacterUtil;

import java.util.ArrayList;
import java.util.List;

public class GhunnaRule implements Rule {

  @Override
  public List<Result> checkAyah(String ayah) {
    List<Result> results = new ArrayList<>();
    int length = ayah.length();
    int startPos;
    int endPos;
    ResultType mode = ResultType.GHUNNA;
    for (int i = 0; i < length; i++) {
      int[] next = CharacterUtil.getNextChars(ayah, i);
      int currentChar = next[0];
      if ((currentChar == CharacterUtil.NOON ||
          currentChar == CharacterUtil.MEEM) &&
          (next[1] == CharacterUtil.SHADDA ||
              next[2] == CharacterUtil.SHADDA)) {
        startPos = i;
        endPos = i + CharacterUtil.findRemainingMarks(next);
        results.add(new Result(mode, startPos, endPos));
      }
    }
    return results;
  }
}
