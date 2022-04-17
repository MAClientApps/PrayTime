package com.code.apppraytime.tajweed.exporter;


import com.code.apppraytime.tajweed.model.Result;

import java.util.List;

public interface Exporter {
  String export(String ayah, List<Result> results);
}
