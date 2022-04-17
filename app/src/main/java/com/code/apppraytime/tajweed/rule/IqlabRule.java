package com.code.apppraytime.tajweed.rule;


import com.code.apppraytime.tajweed.model.Result;
import com.code.apppraytime.tajweed.model.ResultType;
import com.code.apppraytime.tajweed.model.TwoPartResult;
import com.code.apppraytime.tajweed.util.CharacterInfo;
import com.code.apppraytime.tajweed.util.CharacterUtil;

import java.util.ArrayList;
import java.util.List;

public class IqlabRule implements Rule {
  public IqlabRule() {

  }

  @Override
  public List<Result> checkAyah(String ayah) {
    List<Result> results = new ArrayList<>();
    int index = -1;
    while ((index = (ayah.indexOf(CharacterUtil.BA, index + 1))) > -1) {
      CharacterInfo previousCharacter = CharacterUtil.getPreviousCharacter(ayah, index);
      int[] prev = CharacterUtil.getPreviousChars(ayah, index);
      int previous = previousCharacter.character;
      if ((CharacterUtil.isTanween(previous) ||
          previous == CharacterUtil.NOON)) {
        results.add(new TwoPartResult(ResultType.IQLAB, index, index + 1,
            ResultType.IQLAB_NOT_PRONOUNCED, previousCharacter.index,
            previousCharacter.index + 1));
      } else if ((previous == CharacterUtil.SMALL_HIGH_MEEM_ISOLATED)) {
        // the letter should be pronounced as a meem - let's double check it's above a noon
        CharacterInfo actual = CharacterUtil.getPreviousCharacter(ayah, previousCharacter.index);
        if (actual.character == CharacterUtil.NOON) {
          results.add(new TwoPartResult(ResultType.IQLAB, previousCharacter.index, index + 1,
              ResultType.IQLAB_NOT_PRONOUNCED, actual.index, previousCharacter.index));
        }
      }
    }
    return results;
  }
}
