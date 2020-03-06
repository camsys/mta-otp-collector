package com.camsys.shims.util.source;

import com.camsys.shims.gtfsrt.alerts.siri.transformer.GtfsrtToSiriTransformer;
import uk.org.siri.siri.Siri;


/**
 * Transform SIRI according to the given transformer.
 */
public class TransformingSiriSource implements SiriSource {

    // input
    protected UpdatingGtfsRealtimeSource _input;
    // transformer
    protected GtfsrtToSiriTransformer _transformer;

    protected Siri _siri;

    public void setInput(UpdatingGtfsRealtimeSource input) {_input = input; }

    public void setTransformer(GtfsrtToSiriTransformer transformer) {
        _transformer = transformer;
    }

    @Override
    public void update() {
        _input.update();
        if (_input.getFeed() == null) return;

        _siri = _transformer.transform(_input.getFeed());
    }

    @Override
    public Siri getSiri() { return _siri; }


}
