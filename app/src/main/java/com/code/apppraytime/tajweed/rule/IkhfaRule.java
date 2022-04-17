package com.code.apppraytime.tajweed.rule;

import com.code.apppraytime.tajweed.model.Result;
import com.code.apppraytime.tajweed.model.ResultType;
import com.code.apppraytime.tajweed.util.CharacterUtil;

import java.util.ArrayList;
import java.util.List;

public class IkhfaRule implements Rule {

  private static final Character TA = 0x062a;
  private static final Character THAA = 0x062b;
  private static final Character JEEM = CharacterUtil.JEEM;
  private static final Character DAAL = CharacterUtil.DAAL;
  private static final Character ZAAL = 0x0630;
  private static final Character ZA = 0x0632;
  private static final Character SEEN = 0x0633;
  private static final Character SHEEN = 0x0634;
  private static final Character SAAD = 0x0635;
  private static final Character DAAD = 0x0636;
  private static final Character TAA = CharacterUtil.TAA;
  private static final Character ZAA = 0x0638;
  private static final Character FAA = 0x0641;
  private static final Character QAAF = CharacterUtil.QAAF;
  private static final Character KAAF = 0x0643;
  private static final Character[] LETTERS_OF_IKHFA = { TA, THAA, JEEM, DAAL, ZAAL,
      ZA, SEEN, SHEEN, SAAD, DAAD, TAA, ZAA, FAA, QAAF, KAAF };

  public IkhfaRule() {

  }


  @Override
  public List<Result> checkAyah(String ayah) {
    List<Result> results = new ArrayList<>();
    int length = ayah.length();
    int startPos, endPos;
    ResultType mode = ResultType.IKHFA;
    for (int i = 0; i < length; i++) {
      int[] next = CharacterUtil.getNextChars(ayah, i);
      int[] previous = CharacterUtil.getPreviousChars(ayah, i);
      //If noon sakin or tanween is followed by a letter of Ikhfa
      if (((CharacterUtil.isTanween(next[0]) ||
              CharacterUtil.isNoonSaakin(next)) &&
              isIkhfaLetter(next[CharacterUtil.findNextLetterPronounced(next)]))) {
        startPos = i;
        endPos = i + CharacterUtil.findRemainingMarks(next);
        results.add(new Result(mode, startPos, endPos));
      }
    }
    return results;
  }

  private boolean isIkhfaLetter(int thisChar){
    return (thisChar == TA || thisChar == TAA || thisChar == JEEM || thisChar == DAAL ||
            thisChar == ZAAL || thisChar == ZA || thisChar == SEEN || thisChar == THAA ||
            thisChar == SHEEN || thisChar == SAAD || thisChar == DAAD || thisChar == ZAA ||
            thisChar == FAA || thisChar == QAAF || thisChar == KAAF);
  }
}
