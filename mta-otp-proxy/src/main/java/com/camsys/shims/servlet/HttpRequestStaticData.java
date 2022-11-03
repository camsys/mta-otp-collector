/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package com.camsys.shims.servlet;

import com.camsys.shims.schedule.transformer.CsvRecordReader;
import com.camsys.shims.schedule.transformer.CsvToJsonTransformer;

import java.util.List;

/**
 * serve static JSON data backed by arbitrary CSV at a url.
 */
public class HttpRequestStaticData extends AbstractHttpRequestStaticData<List<Object>> {

    private String _sourceUrl = null;

    private String _profile = null;

    public void setSourceUrl(String url) {
        _sourceUrl = url;
    }

    private CsvRecordReader<Object> _reader = null;

    public void setCsvReader(CsvRecordReader reader) {
        _reader = reader;
    }

    public void setProfile(String profile) {
        _profile = profile;
    }

    private CsvToJsonTransformer _transformer = null;

    @Override
    protected List<Object> getData(String routeId) {
        // lookup injected source file
        // download and load
        getTransformer().loadUrl(_sourceUrl);
        // filter
        // transform
        return getTransformer().transform(routeId);
    }

    protected CsvToJsonTransformer<Object> getTransformer() {
        if (_transformer == null) {
            _transformer = new CsvToJsonTransformer(_reader, _profile);
        }
        return _transformer;
    }

}
