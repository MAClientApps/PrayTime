package com.code.apppraytime.tajweed.exporter;

import com.code.apppraytime.tajweed.model.Result;
import com.code.apppraytime.tajweed.model.ResultType;
import com.code.apppraytime.tajweed.model.ResultUtil;
import com.code.apppraytime.tajweed.model.TwoPartResult;
import java.util.List;

public class HtmlExporter implements Exporter {

  @Override
  public String export(String ayah, List<Result> results) {
    ResultUtil.INSTANCE.fixOverlappingRules(results);

    StringBuilder buffer = new StringBuilder();
    int currentIndex = 0;
    for (Result result : results) {
      int start = result.getMinimumStartingPosition();
      if (start > currentIndex) {
        buffer.append(ayah.substring(currentIndex, start));
      }

      start = result.getMinimumStartingPosition();
      if (result instanceof TwoPartResult) {
        TwoPartResult twoPartResult = (TwoPartResult) result;
        if (start == twoPartResult.start) {
          // first, then second
          appendRule(buffer, ayah, twoPartResult.resultType,
              twoPartResult.start, twoPartResult.ending);
          if (twoPartResult.secondStart - twoPartResult.ending > 0) {
            buffer.append(ayah.substring(twoPartResult.ending, twoPartResult.secondStart));
          }
          appendRule(buffer, ayah, twoPartResult.secondResultType,
              twoPartResult.secondStart, twoPartResult.secondEnding);
        } else {
          // second, then first
          appendRule(buffer, ayah, twoPartResult.secondResultType,
              twoPartResult.secondStart, twoPartResult.secondEnding);
          if (twoPartResult.start - twoPartResult.secondEnding > 0) {
            buffer.append(ayah.substring(twoPartResult.secondEnding, twoPartResult.start));
          }
          appendRule(buffer, ayah,
              twoPartResult.resultType, twoPartResult.start, twoPartResult.ending);
        }
      } else {
        appendRule(buffer, ayah, result.resultType, result.start, result.ending);
      }
      currentIndex = result.getMaximumEndingPosition();
    }

    buffer.append(ayah.substring(currentIndex));
    buffer.append(" ");
    return buffer.toString();
  }

  private void appendRule(StringBuilder buffer, String ayah, ResultType type, int start, int end) {
    buffer.append("<font color=\"#");
    buffer.append(getColorForRule(type));
    buffer.append("\">");
    buffer.append(ayah.substring(start, end));
    buffer.append("</font>");
  }

  private String getColorForRule(ResultType type) {
    return type.color;
  }
}
