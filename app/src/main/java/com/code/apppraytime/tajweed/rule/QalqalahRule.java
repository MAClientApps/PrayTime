package com.code.apppraytime.tajweed.rule;

import com.code.apppraytime.tajweed.model.Result;
import com.code.apppraytime.tajweed.model.ResultType;
import com.code.apppraytime.tajweed.util.CharacterUtil;
import java.util.ArrayList;
import java.util.List;

public class QalqalahRule implements Rule {

  public QalqalahRule() {

  }

  @Override
  public List<Result> checkAyah(String ayah) {
    List<Result> results = new ArrayList<>();
    int length = ayah.length();
    int startPos, endPos;
    ResultType mode = ResultType.QALQALAH;
    for (int i = 0; i < length; i++) {
      int[] next = CharacterUtil.getNextChars(ayah, i);
      int currentChar = next[0];

      if ((currentChar == CharacterUtil.DAAL ||
          currentChar == CharacterUtil.BA ||
          currentChar == CharacterUtil.JEEM ||
          currentChar == CharacterUtil.QAAF ||
          currentChar == CharacterUtil.TAA) &&
          ((next[1] == CharacterUtil.SUKUN || next[1] == CharacterUtil.JAZM) ||
              next[1] == ' ' ||
              CharacterUtil.isLetter(next[1]) ||
              weStopping(next))) {
        startPos = i;
        endPos = i + 1;
        if ((next[1] == CharacterUtil.SUKUN || next[1] == CharacterUtil.JAZM) ) {
          endPos++;
        }
        if ((next[1] == CharacterUtil.SUKUN || next[1] == CharacterUtil.JAZM) || next[1] == ' ' || CharacterUtil.isLetter(next[1])) {
          for (int j = 1; j < next.length - 2 && next[j] != 0; j++) {
            if (!(CharacterUtil.isLetter(next[j]) &&
                (next[j + 1] == CharacterUtil.SHADDA || next[j + 2] == CharacterUtil.SHADDA))) {
              results.add(new Result(mode, startPos, endPos));
            } else {
              break;
            }
          }
        } else {
          results.add(new Result(mode, startPos, endPos));
        }
      }
    }
    return results;
  }

  private boolean weStopping(int[] next) {
    for (int i = 1; i < next.length; i++) {
      if ((CharacterUtil.isEndMark(
          next[i]) && next[i] != CharacterUtil.SMALL_LAAM_ALEF &&
          ((next[i] != CharacterUtil.SMALL_THREE_DOTS))) || next[i] == 0) {
        return true;
      }
      if (CharacterUtil.isLetter(next[i])) {
        break;
      }
    }
    return false;
  }
}
