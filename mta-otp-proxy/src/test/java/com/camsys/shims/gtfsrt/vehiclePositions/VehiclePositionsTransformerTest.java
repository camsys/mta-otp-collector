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
package com.camsys.shims.gtfsrt.vehiclePositions;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import org.junit.Test;
import org.onebusaway.geospatial.model.CoordinatePoint;
import org.onebusaway.geospatial.services.PolylineEncoder;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class VehiclePositionsTransformerTest {

    private final String lirrShape8 = "ccvwFxwrbMp@_Cp@aCp@_Cb@eCXkCl@aCt@}Br@}Br@_Ct@}Br@}Bt@}Br@_Ct@}Br@}Br@}Bt@_Cr@}Bt@}Br@}Bt@_Cr@}Bt@}Bp@_Cr@}Br@_Cr@_Ch@eCd@eC^iCZkCPmCNoCNmCNmCNoCNmCNmCNoCNmCPoCNmCNmCNoCNmCNmCNoCNmCNoCNmCNoCNmCLoCLmCLoCLmCFoCAoCCoCIoCQmCUmC[kCc@gCg@eCi@eCm@aCm@cCm@aCo@aCs@_Cs@}Bs@_Cu@}Bs@}Bu@_Cs@}Bu@}Bu@}Bs@}Bo@aCo@aCk@cCg@eCa@iCa@gC[kC[kC]kC[iCYkCYmCYkCWkCYkCYkC[kCYkCYkCYkCYmC[kCYkCYkCYkC[kCYkCYkCYkCYkCWmCYkCYkCWkCYkCWmCQmCIoC@oCNmCZkCf@eCl@cCr@}Bv@{B|@wB`AsBdAmBjAiBlAcBpA_BpA_BrA}ArA}ApA}AtAyAtAyAtAyAtAyAtAyArA{ArA{AtA{ArA{ArA{AtA{ArA{ArA{AtAyAtAwAnAcBjAeBdAoB~@uBx@yBv@{Br@}Bt@}Bt@_Cr@}Bt@}Bt@}Br@}Bt@}Bt@}Bt@}Br@}Bt@}Br@}Br@_Cp@aCn@_Cn@aCn@aCl@aCn@aCn@aCn@cCn@aCl@aCn@aCn@aCn@aCl@aCn@aCn@aCn@aCn@aCl@aCn@aCn@aCn@aCn@aCn@aCl@aCn@aCn@aCn@aCn@aCl@aCn@aCn@aCn@aCn@aCl@aCn@aCn@aCn@aCn@aCn@aCl@aCn@aCn@aCn@aCn@aCl@aCn@aCn@aCn@aCn@aCn@aCl@aCn@aCn@aCn@aCn@aCl@aCn@aCn@aCn@aCn@aCn@aCp@_Ct@}Bx@{Bz@wB~@sB`AsBdAmBhAkBhAiBnAcBnAaBpA}ArA}ArA}ArA{ArA}ArA{ArA}ArA}ArA{ArA}ArA{ArA}ApA{ArA}ArA{ArA}ArA}ArA{ArA}ArA{ArA}ArA{ArA}ArA}ArA{ArA}ApA{ArA}ArA{ArA}ArA{ArA}ArA{ArA_BnA_BlAeBjAgBdAmB`AsBx@yBr@_Cj@cC^iCPmCBoCIoCYkC[kC[iC_@iCc@gCe@gCe@gC`@iGg@eCg@eCi@eCi@eCi@eCg@cCi@eCo@aCw@{Bs@}Bk@eCa@gC_@iC_@iCa@iC_@iC_@gCe@gCe@gCa@gCa@iCa@iC_@iCa@gC_@iCa@iC_@iCa@iC_@iC[iC_@iC_@iCa@iC_@iCa@gC_@iCGoCBoCNmCZkCd@eCr@_Cz@wBdAmBnAcBrA{ArA{AtA{ArA{ArA{AtAyAvAuAvAuAxAuAvAsAvAuAxAsAvAsAxAsAvAuAxAsAvAsAxAuAvAsAxAsAvAuAvAsAxAsAvAuAxAsAxAqA|AiA~AcAfBy@dBs@dBq@fBm@fBm@dBs@fBs@dBs@dBq@dBu@dBw@`B}@`B_A~AeA`BcA~AeA~AeA~AcA~AeA~AeA~AcA`BeA~AeA~AcA~AeA~AeA~AcA~AeA`BeA~AcA~AeA~AeA~AcA~AeA~AeA~AcA`BeA~AeA~AcA~AeA~AeA~AeA~AcA`BeA~AcA~AgAzAkAtAwAlAcBfAmB`AsBx@yBr@_Cj@cCf@eC\\iCVkCNoCHoC@oC?oC?oC?oC?oC?oC?oCAoC?oC?oC?qC?oC?oC?oC?oC?oC?oC?oCAoC?oC?oC?qC?oC?oC?oCAoC?oC@oCBoCHoCNmCRmCZkC^iCb@gCd@gCd@eCb@gCd@gCd@eCd@gCd@gCb@gCd@eCd@gCd@gCd@eCd@gCd@gCb@gCd@eCd@gCd@gCd@gCPcAX_@d@gCb@gCd@eCd@gCd@gCb@gC`@iC\\iCb@gCd@gCb@eCd@gCd@gCb@gC^iC\\iCVkCTmCRmCNmCPoCPmCNmCPmCNmCPmCNoCPmCPmCNmCRmCPmCRmCTmCTkCVmCVkCVkCVmCVkCVkCVmCVkCVkCTmCLoCBmCAqCImCQmCUmCWkCUmCUmCUkCUmCUkCUmCUmCWkCUmCUkCUmCUmCUkCUmCUkCWmCUmCUkCUmCSkCSmCMoCIoCEoCCoC@oC@oCBoC@oCDoCDoCDoCHoCFoCFmCFoCDoCDoCFoCDoCDoCDoCDoCDoCDoCFoCDoCDoCDoCDoCDoCDoCDoCDoCBoCDoCDoCBoCDoCBoCDoCBoCDoCDoCBoCBoCDoCBoCDoCDoCBoCDoCFoCDoCDoCFmCDoCFoCDoCDoCBoCBoC?oC@oC?qCBoC@oCBoCBoC@oC@oC?oC@oC@oC?oC@oC?oC@oC?qC@oC?oCAoCCoCCoCEoCGoCEoCEoCGoCEoCEmCGoCGoCGoCKoCMmCMoCQmCQmCSmCWkCUmCUkCWmCWkCYkCWmCYiCYkC[mCYiCYkCYkC[kCYkCYkCYkC[kCYkCYkCYkC[kCYkCYkCYkCYkC[kCYkCYkC]iC[kC_@iC]iC_@iC_@iC_@iC_@iC_@iC_@iCa@gCa@iC_@iC_@iC_@iC_@iC_@iC_@iC_@iC]iC_@iC]iC]kC_@iC_@iC]iC_@iC_@iC_@iC_@iC_@iC_@iC_@iC]iC_@iC_@iC_@iC_@iC_@iC_@iCa@gC_@iCa@iC_@iC_@iC]iC_@iC_@iC]iC_@iC_@kC]iC]iC]iC]kC_@iC_@iC_@iC]iC_@iC_@iC]iC_@iC_@iC_@iC_@iC]kC_@iC[iCYkCWmCWkCWkCWmCWkCWmCUkCSmCQmCQmCSmCSmCUmCUkCUmCUmCUkCUmCUmCUkCUmCUmCSkCUmCUmCSmCOmCMmCIoCIoCIoCIoCImCIoCIoCIoCKmCIoCIoCIoCIoCImCIoCIoCIoCIoCImCIoCIoCKoCIoCImCIoCIoCIoCGoCIoCImCGoCIoCIoCGoCIoCImCGoCIoCIoCIoCGoCImCIoCGoCIoCIoCGoCImCIoCGoCIoCIoCGoCIoCImCGoCIoCIoCGoCIoCImCIoCGoCIoCIoCGoCImCIoCGoCIoCIoCGoCIoCImCIoCIoCIoCIoCImCIoCIoCKoCIoCImCIoCIoCGoCGoCGoCImCIoCKoCIoCIoCKmCKoCKoCMmCKoCKoCKmCIoCGoCIoCEoCGoCCoCEoCCoCCoCEoCCoCCoCEoCCoCCoCCoCCoCEoCCoCCoCCoCEoCCoCCqCEoCCoCCoCCoCCoCEoCCoCCoCEoCCoCEoCIoCKmCOoCSmCWkC[kC_@iCa@gCe@gCe@eCg@gCg@eCg@eCg@eCg@eCg@gCg@eCg@eCg@eCg@eCe@gCg@eCg@eCg@eCg@eCg@gCg@eCg@eCg@eCe@eCg@gCg@eCi@eCg@eCi@eCi@cCi@eCi@eCg@eCg@eCe@eCc@gCe@gCe@gCe@eCg@gCg@eCe@eCg@eCe@gCg@gCc@eCe@gCe@gCc@gCe@gCc@gCe@gCc@gCc@gCe@gCc@gCc@gCc@gCe@gCc@gCc@gCc@gCe@gCc@gCc@gCc@gCe@gCc@gCc@gCe@gCc@gCc@gCe@gCc@eCe@gCc@gCe@gCc@gCe@gCc@gCe@gCc@gCe@gCe@gCc@eCe@gCc@gCe@gCc@gCe@gCc@gCe@gCc@gCc@gCe@eCi@eCk@eCi@cCe@gC";

    private final String lirrShape11 = "wilwFjfp~Ld@fCh@bCj@dCh@dCd@dCb@fCb@fCd@fCb@fCd@fCb@fCd@fCb@fCd@fCb@dCd@fCd@fCb@fCd@fCb@fCd@fCb@fCd@fCb@fCd@fCb@dCd@fCb@fCb@fCd@fCb@fCb@fCd@fCb@fCb@fCb@fCd@fCb@fCb@fCb@fCd@fCb@fCb@fCb@fCd@fCb@fCb@fCd@fCb@fCd@fCb@fCd@fCd@fCb@dCf@fCd@fCf@dCd@dCf@dCf@fCd@dCd@fCd@fCb@fCd@dCf@dCf@dCh@dCh@dCh@bCh@dCf@dCh@dCf@dCf@fCd@dCf@dCf@dCf@dCf@fCf@dCf@dCf@dCf@dCd@fCf@dCf@dCf@dCf@dCf@fCf@dCf@dCf@dCf@dCf@fCd@dCd@fC`@fC^hCZjCVjCRlCNnCJlCHnCDnCBnCDnCBnCBnCDnCBnCBnCBnCBnCDnCBpCBnCDnCBnCBnCBnCDnCBnCBnCBnCBnCDnCBnCBnCDnCBnCBnCDnCBnCFnCDnCHnCFnCHnCJlCJnCJnCLlCJnCJnCJlCHnCHnCJnCHnCHlCFnCFnCFnCHnCHnCHlCHnCJnCHnCHnCHlCHnCHnCHnCHnCHlCHnCFnCHnCHnCFnCHnCHlCFnCHnCHnCFnCHnCHlCHnCFnCHnCHnCFnCHlCHnCFnCHnCHnCFnCHnCHlCFnCHnCHnCFnCHnCHlCFnCHnCHnCHnCFnCHlCHnCFnCHnCHnCFnCHlCHnCFnCHnCHnCHnCHlCHnCJnCHnCHnCHlCHnCHnCHnCHnCHlCHnCHnCHnCHnCJlCHnCHnCHnCHlCHnCHnCHnCHnCLlCNlCRlCTlCTlCRjCTlCTlCTjCTlCTlCTjCTlCTlCTjCTlCRlCRlCPlCPlCRlCTjCVlCVjCVlCVjCVjCVlCXjCZhC^hC\\jC^hC^hC^hC^hC\\hC^hC^hC\\hC^hC^hC^hC\\jC\\hC\\hC\\hC^jC^hC\\hC^hC^hC\\hC^hC^hC`@hC^hC`@fC^hC^hC^hC^hC^hC^hC\\hC^hC^hC^hC^hC^hC^hC^hC\\hC^hC^hC\\jC\\hC^hC\\hC^hC^hC^hC^hC^hC^hC^hC`@hC`@fC^hC^hC^hC^hC^hC^hC\\hC^hCZjC\\hCXjCXjCZjCXjCXjCXjCXjCZjCXjCXjCXjCZjCXjCXjCXjCZjCXjCXjCXhCZlCXjCXhCVlCXjCVjCVlCTjCTlCVjCRlCPlCPlCLnCLlCJnCFnCFnCFnCDlCDnCFnCDnCDnCFnCDnCBnCBnC@nC?nCAnC?pCAnC?nCAnC?nCAnCAnC?nCAnCAnCCnCCnCAnCCnC?pCAnC?nCCnCCnCEnCEnCGnCEnCGlCEnCEnCGnCEnCCnCEnCEnCCnCEnCCnCCnCEnCEnCCnCEnCCnCEnCCnCEnCEnCCnCEnCEnCEnCEnCEnCEnCEnCEnCGnCEnCEnCEnCEnCEnCEnCGnCEnCEnCGnCGlCGnCInCEnCEnCEnCAnCCnCAnCAnCBnCDnCHnCLnCRlCRjCTlCTjCTlCVlCTjCTlCTjCTlCTlCTjCTlCVjCTlCTlCTjCTlCTjCTlCTlCVjCTlCPlCHlC@pCClCMnCUlCWjCWjCWlCWjCWjCWlCWjCWjCWlCUjCUlCSlCQlCSlCOlCQlCQlCOnCQlCOlCQlCOlCQlCQnCOlCSlCUlCWjC]hC_@hCc@fCe@fCe@fCc@dCe@fCc@fC]hCa@hCc@fCe@fCe@fCe@dCc@fCe@fCY^QbAe@fCe@fCe@fCe@dCc@fCe@fCe@fCe@dCe@fCe@fCe@dCc@fCe@fCe@fCe@dCe@fCc@fCe@dCe@fCc@fC_@hC[jCSlCOlCInCCnCAnC?nC@nC?nC?nC?nC?pC?nC?nC@nC?nC?nC?nC?nC?nC?nC?nC?pC?nC?nC@nC?nC?nC?nC?nC?nC?nCAnCInCOnCWjC]hCg@dCk@bCs@~By@xBaArBgAlBmAbBuAvA{AjA_BfA_BbAaBdA_BbA_BdA_BdA_BdA_BbA_BdAaBdA_BbA_BdA_BdA_BbA_BdA_BdA_BbAaBdA_BdA_BbA_BdA_BdA_BbA_BdAaBdA_BbA_BdA_BdA_BbA_BdA_BdAaBbA_BdAaB~@aB|@eBv@eBt@eBp@eBr@gBr@eBr@gBl@gBl@eBp@eBr@gBx@_BbA}AhAyApAyArAwAtAyArAwArAwAtAyArAwArAyAtAwArAyArAwAtAyArAwArAyArAwAtAwArAyAtAwAtAwAtAuAxAsAzAsAzAuAzAsAzAsAzAoAbBeAlB{@vBs@~Be@dC[jCOlCCnCFnC^hC`@fC^hC`@hC^hC^hCZhC^hC`@hC^hC`@hC^hC`@fC^hC`@hC`@hC`@fCd@fCd@fC^fC^hC`@hC^hC^hC`@fCj@dCr@|Bv@zBn@`Ch@dCf@bCh@dCh@dCh@dCf@dCf@dCa@hGd@fCd@fCb@fC^hCZhCZjCXjCHnCCnCQlC_@hCk@bCs@~By@xBaArBeAlBkAfBmAdBoA~AsA~AsAzAsA|AsAzAsA|AsAzAsA|AqAzAsA|AsAzAsA|AsA|AsAzAsA|AsAzAsA|AsAzAsA|AsA|AsAzAsA|AqAzAsA|AsAzAsA|AsAzAsA|AsA|AsAzAsA|AsAzAsA|AsA|AqA|AoA`BoAbBiAhBiAjBeAlBaArB_ArB{@vBy@zBu@|Bq@~Bo@`Co@`Co@`Co@`Co@`Cm@`Co@`Co@`Co@`Co@`Cm@`Co@`Co@`Co@`Co@`Co@`Cm@`Co@`Co@`Co@`Co@`Cm@`Co@`Co@`Co@`Co@`Co@`Cm@`Co@`Co@`Co@`Co@`Cm@`Co@`Co@`Co@`Co@`Cm@`Co@`Co@`Co@`Co@`Co@`Cm@`Co@`Co@`Co@`Co@`Cm@`Co@`Co@`Co@`Cm@`Co@`Co@bCo@`Co@`Cm@`Co@`Co@`Co@~Bq@`Cs@~Bs@|Bu@|Bs@|Bu@|Bu@|Bu@|Bs@|Bu@|Bu@|Bs@|Bu@~Bu@|Bs@|Bw@zBy@xB_AtBeAnBkAdBoAbBuAvAuAxAsAzAsAzAuAzAsAzAsAzAuAzAsAzAsAzAuAxAuAxAuAxAuAxAuAxAqA|AsA|AsA|AqA~AqA~AmAbBkAhBeAlBaArB}@vBw@zBs@|Bm@bCg@dC[jCOlCAnCHnCPlCVlCXjCVjCXjCXjCVlCXjCXjCXjCXjCZjCXjCXjCXjCZjCXlCXjCXjCXjCZjCXjCXjCVjCXjCXlCXjCZhC\\jCZjCZjC`@fC`@hCf@dCj@bCn@`Cn@`Cr@|Bt@|Bt@|Br@|Bt@~Br@|Bt@|Br@~Br@|Br@~Bn@`Cl@`Cl@bCl@`Ch@dCf@dCb@fCZjCTlCPlCHnCBnC@nCGnCMlCMnCMlCMnCOlCOnCOlCOnCOlCOnCOlCOlCOnCOlCOlCQnCOlCOnCOlCOlCOnCOlCOlCOnCQlC[jC_@hCe@dCi@dCs@~Bs@~Bs@|Bq@~Bu@|Bs@|Bu@~Bs@|Bu@|Bs@|Bu@~Bs@|Bs@|Bu@|Bs@~Bu@|Bs@|Bu@|Bs@~Bs@|Bu@|Bm@`CYjCc@dCq@~Bq@`Cq@~B";

    private VehiclePositionsTransformer transformer = new VehiclePositionsTransformer();

    @Test
    public void testLirrOrientation1() {
        assertOrientationMatch(lirrShape8, 40.679688f, -73.42781f, 85);
    }

    // at penn station
    @Test
    public void testLirrOrientation2() {
        assertOrientationMatch(lirrShape11, 40.74926f, -73.989685f, 289);
    }

    // too far from line
    @Test
    public void testOrientationTooFar() {
        LineString geom = decode(lirrShape11);
        float lat = 40.8939f, lon = -73.4462f;
        Float orientation = transformer.calculateBearing(geom, lat, lon);
        assertNull(orientation);
    }

    private void assertOrientationMatch(String shapeId, float lat, float lon, double expected) {
        LineString geom = decode(shapeId);
        float orientation = transformer.calculateBearing(geom, lat, lon);
        assertEquals(expected, orientation, 1d);
    }

    private LineString decode(String polyline) {
        List<CoordinatePoint> points = PolylineEncoder.decode(polyline);
        Coordinate[] coords = new Coordinate[points.size()];
        for (int i = 0; i < points.size(); i++) {
            CoordinatePoint point = points.get(i);
            coords[i] = new Coordinate(point.getLon(), point.getLat());
        }
        return new GeometryFactory().createLineString(coords);
    }

}
