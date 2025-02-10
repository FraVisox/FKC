package it.unidp.dei.OURS.CAPPDELTAxx;

import it.unidp.dei.OURS.CAPP.KPELLCAPP;
import it.unidp.dei.CHENETAL.CHEN;

//It is a wrapper around KPELLCAPP, as it only selects epsilon based on delta.
public class KPELLCAPPDELTAxx extends KPELLCAPP
{
    public KPELLCAPPDELTAxx(double _beta, double _delta, int[] _ki) {
        super(_beta, _delta*(1+_beta)*(1+2*CHEN.alfa), _ki);
    }
}

