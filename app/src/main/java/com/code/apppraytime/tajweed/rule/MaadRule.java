package com.code.apppraytime.tajweed.rule;

import com.code.apppraytime.tajweed.model.Result;
import com.code.apppraytime.tajweed.model.ResultType;
import com.code.apppraytime.tajweed.util.CharacterUtil;
import com.code.apppraytime.tajweed.util.Characters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MaadRule implements Rule {

  public MaadRule() {

  }

  private static final List<Character> MAAD_LETTERS =
      Arrays.asList(Characters.ALEF, Characters.WAW, Characters.YA, Characters.ENDING_YA,
          Characters.ALEF_SUGHRA, Characters.WAW_SUGHRA, Characters.YA_SUGHRA);

  @Override
  public List<Result> checkAyah(String ayah) {
    List<Result> results = new ArrayList<>();
    int[] characters = ayah.codePoints().toArray();
    for (int i = 0; i < characters.length; i++) {
      boolean isExplicitMaad = isExplicitMaad(characters, i);
      if (isHarfMaad(characters, i)) {
        if (isExplicitMaad) {
          // 4 characters
          results.add(new Result(ResultType.MAAD_MUNFASSIL_MUTASSIL, i, i + 2));
          continue;
        }
        // we have a maad - now let's see which type
        int nextCharacter = CharacterUtil.getNextLetter(characters, i);
        int nextTashkeel = nextCharacter > 0 ?
            CharacterUtil.getTashkeelForLetter(characters, nextCharacter) : -1;
        if (nextTashkeel > -1 && characters[nextTashkeel] == CharacterUtil.SHADDA) {
          results.add(new Result(ResultType.MAAD_LONG, i, i + 1));
        } else if (nextCharacter > -1 && characters[nextCharacter] == CharacterUtil.HAMZA) {
          results.add(new Result(ResultType.MAAD_MUNFASSIL_MUTASSIL, i, i + 1));
        } else if ((nextTashkeel > -1 && characters[nextTashkeel] == CharacterUtil.SUKUN) ||
            (nextCharacter > -1 && CharacterUtil.getNextLetter(characters, nextCharacter) == -1)) {
          results.add(new Result(ResultType.MAAD_SUKOON, i, i + 1));
        } else if (characters[i] == Characters.ALEF_SUGHRA ||
            characters[i] == Characters.WAW_SUGHRA ||
            characters[i] == Characters.YA_SUGHRA) {
          results.add(new Result(ResultType.MAAD_SILA_SUGHRA, i, i + 1));
        }
        // else, this is a normal maad, which isn't highlighted in the madani mus7af
      } else if (isExplicitMaad) {
        // a non-alef/waw/ya that has a maad - means we're in one of 7uroof al mutaqata3a
        results.add(new Result(ResultType.MAAD_LONG, i, i + 2));
      }
    }
    return results;
  }

  private boolean isHarfMaad(int[] characters, int index) {
    if (MAAD_LETTERS.contains((char) characters[index])) {
      int tashkeel = CharacterUtil.getTashkeelForLetter(characters, index);
      if (tashkeel == -1) {
        int previous = CharacterUtil.getPreviousLetter(characters, index);
        if (previous > -1) {
          tashkeel = CharacterUtil.getTashkeelForLetter(characters, previous);
          return tashkeel > -1 &&
              ((characters[tashkeel] == CharacterUtil.FATHA &&
                  (characters[index] == Characters.ALEF ||
                      characters[index] == Characters.ALEF_SUGHRA)) ||
                  (characters[tashkeel] == CharacterUtil.DAMMA &&
                      (characters[index] == Characters.WAW ||
                          characters[index] == Characters.WAW_SUGHRA)) ||
                  (characters[tashkeel] == CharacterUtil.KASRA &&
                      (characters[index] == Characters.YA ||
                          characters[index] == Characters.ENDING_YA ||
                          characters[index] == Characters.YA_SUGHRA)));
        }
      } else if (characters[tashkeel] == CharacterUtil.MAAD_MARKER) {
        // explicit maad
        return true;
      }
    }
    return false;
  }

  private boolean isExplicitMaad(int[] characters, int index) {
    int tashkeel = CharacterUtil.getTashkeelForLetter(characters, index);
    return tashkeel > - 1 && characters[tashkeel] == CharacterUtil.MAAD_MARKER;
  }
}
