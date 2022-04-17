package com.code.apppraytime.tajweed.rule;

import com.code.apppraytime.tajweed.model.Result;
import com.code.apppraytime.tajweed.model.ResultType;
import com.code.apppraytime.tajweed.util.CharacterUtil;
import java.util.ArrayList;
import java.util.List;

public class MeemRule implements Rule {

  public MeemRule() {

  }

  @Override
  public List<Result> checkAyah(String ayah) {
    List<Result> results = new ArrayList<>();
    int length = ayah.length();
    int startPos, endPos;
    ResultType idghamMode = ResultType.MEEM_IDGHAM;
    ResultType ikhfaMode = ResultType.MEEM_IKHFA;
    for (int i = 0; i < length; i++) {
      int[] next = CharacterUtil.getNextChars(ayah, i);
      if (CharacterUtil.isMeemSaakin(next)) {
        for (int j = 1; j < next.length && next[j] != 0; j++) {
          if (CharacterUtil.isLetter(next[j])) {
            if (next[j] == CharacterUtil.MEEM || next[j] == CharacterUtil.BA) {
              startPos = i;
              endPos = i + CharacterUtil.findRemainingMarks(next);
              if (next[j] == CharacterUtil.MEEM) {
                results.add(new Result(idghamMode, startPos, endPos));
              } else {
                results.add(new Result(ikhfaMode, startPos, endPos));
              }
            } else {
              break;
            }
          }
        }
      }
    }
    return results;
  }
}
