package com.code.apppraytime.tajweed.rule;

import com.code.apppraytime.tajweed.model.Result;
import com.code.apppraytime.tajweed.model.ResultType;
import com.code.apppraytime.tajweed.model.TwoPartResult;
import com.code.apppraytime.tajweed.util.CharacterInfo;
import com.code.apppraytime.tajweed.util.CharacterUtil;

import java.util.ArrayList;
import java.util.List;

public class IdghamRule implements Rule {

  public IdghamRule() {

  }
  // with ghunna
  private static final Character YA = 0x064a;
  private static final Character MEEM = CharacterUtil.MEEM;
  private static final Character WAW = 0x0648;
  private static final Character NOON = CharacterUtil.NOON;

  // without ghunna
  private static final Character RA = 0x0631;
  private static final Character LAM = 0x0644;

  @Override
  public List<Result> checkAyah(String ayah) {
    List<Result> results = new ArrayList<>();
    int[] characters = ayah.codePoints().toArray();
    for (int i = 0; i < characters.length; i++) {
      int current = characters[i];
      boolean isYarmaloon = isYarmaloon(current);
      if (isYarmaloon) {
        int previousMatchPosition = isValidIdgham(ayah, i);
        if (previousMatchPosition >= 0) {
          boolean withGhunna = (isGhunnaYarmaloon(current));
          if (withGhunna) {
            results.add(new TwoPartResult(ResultType.IDGHAM_WITH_GHUNNA, i, i + 1,
                ResultType.IDGHAM_NOT_PRONOUNCED, previousMatchPosition,
                previousMatchPosition + 1));
          } else {
            results.add(new Result(ResultType.IDGHAM_WITHOUT_GHUNNA,
                previousMatchPosition, previousMatchPosition + 1));
          }
        }
      }
    }
    return results;
  }

  private int isValidIdgham(String ayah, int index) {
    CharacterInfo previousCharacter = CharacterUtil.getPreviousCharacter(ayah, index);
    boolean result = false;
    int previousIndex = previousCharacter.index;
    int previous = previousCharacter.character;
    if (previous == CharacterUtil.FATHA_TANWEEN ||
        previous == CharacterUtil.DAMMA_TANWEEN ||
        previous == CharacterUtil.KASRA_TANWEEN ||
        previous == NOON) {
      result = true;
    } else if (previousCharacter.index > 0 &&
        (previous == CharacterUtil.ALEF_LAYINA || previous == CharacterUtil.ALEF)) {
      previous = ayah.codePointBefore(previousCharacter.index);
      result = previous == CharacterUtil.FATHA_TANWEEN;
      previousIndex = previousCharacter.index - 1;
    }
    return result ? previousIndex : -1;
  }

  private boolean isYarmaloon(int thisChar){
    return (thisChar == RA || thisChar == LAM ||
            thisChar == YA || thisChar == NOON || thisChar == MEEM || thisChar == WAW);
  }

  private boolean isGhunnaYarmaloon(int thisChar){
    return (thisChar == YA || thisChar == NOON || thisChar == MEEM || thisChar == WAW);
  }
}
