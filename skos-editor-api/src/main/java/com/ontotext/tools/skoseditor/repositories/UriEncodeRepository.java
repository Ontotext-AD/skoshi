package com.ontotext.tools.skoseditor.repositories;

import java.util.Map;

public interface UriEncodeRepository {

    void fillMaps(Map<String, String> ns2prefix, Map<String, String> prefix2ns);

}
