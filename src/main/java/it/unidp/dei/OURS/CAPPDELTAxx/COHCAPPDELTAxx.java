package it.unidp.dei.OURS.CAPPDELTAxx;

import it.unidp.dei.OURS.CAPP.COHCAPP;
import it.unidp.dei.CHENETAL.CHEN;

//It is a wrapper around COHCAPP, as it only selects epsilon based on delta.
public class COHCAPPDELTAxx extends COHCAPP
{
    public COHCAPPDELTAxx(double _beta, double _delta, int[] _ki) {
        super(_beta, _delta*(1+_beta)*(1+2*CHEN.alfa), _ki);
    }
}

