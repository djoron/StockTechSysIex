/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author atlantis
 */
public class StockGoogle {
  
public void StockGoogle() {};
    
String id;  // Google ID
String t; // Symbol
String e; // Exchange
String l; // Last Price / Close price
String l_fix;
String l_cur; 
String s;
String ltt; // Last Trade Time
String lt; // Last trade format: "Jan 22, 4:00PM EST"
String lt_dts; // Last Date Time trade format: "2016-01-22T16:00:01Z"
String c; // Change
String c_fix;
String cp;
String cp_fix;
String ccol;
String pcls_fix;
String el;
String el_fix;
String el_cur;
String elt;
String ec;
String ec_fix;
String ecp;
String ecp_fix;
String eccol;
String div;
String yld;
String eo;
String delay;
String op; // Price Open
String hi; // Price High
String lo; // Price Low
String vo; // Volume. If 2.5B - 2.5billion shares
String avvo; // average volume
String hi52; 
String lo52;
String mc; // Market cap
String pe; // Price per earnings
String fwpe;
String beta;
String eps; // Earnings per share
String shares; // Total shares
String inst_own; // Institution Shares own %
String name; // Company or index name
String type; // Usually type Company.

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getL_fix() {
        return l_fix;
    }

    public void setL_fix(String l_fix) {
        this.l_fix = l_fix;
    }

    public String getL_cur() {
        return l_cur;
    }

    public void setL_cur(String l_cur) {
        this.l_cur = l_cur;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getLtt() {
        return ltt;
    }

    public void setLtt(String ltt) {
        this.ltt = ltt;
    }

    public String getLt() {
        return lt;
    }

    public void setLt(String lt) {
        this.lt = lt;
    }

    public String getLt_dts() {
        return lt_dts;
    }

    public void setLt_dts(String lt_dts) {
        this.lt_dts = lt_dts;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getC_fix() {
        return c_fix;
    }

    public void setC_fix(String c_fix) {
        this.c_fix = c_fix;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getCp_fix() {
        return cp_fix;
    }

    public void setCp_fix(String cp_fix) {
        this.cp_fix = cp_fix;
    }

    public String getCcol() {
        return ccol;
    }

    public void setCcol(String ccol) {
        this.ccol = ccol;
    }

    public String getPcls_fix() {
        return pcls_fix;
    }

    public void setPcls_fix(String pcls_fix) {
        this.pcls_fix = pcls_fix;
    }

    public String getEl() {
        return el;
    }

    public void setEl(String el) {
        this.el = el;
    }

    public String getEl_fix() {
        return el_fix;
    }

    public void setEl_fix(String el_fix) {
        this.el_fix = el_fix;
    }

    public String getEl_cur() {
        return el_cur;
    }

    public void setEl_cur(String el_cur) {
        this.el_cur = el_cur;
    }

    public String getElt() {
        return elt;
    }

    public void setElt(String elt) {
        this.elt = elt;
    }

    public String getEc() {
        return ec;
    }

    public void setEc(String ec) {
        this.ec = ec;
    }

    public String getEc_fix() {
        return ec_fix;
    }

    public void setEc_fix(String ec_fix) {
        this.ec_fix = ec_fix;
    }

    public String getEcp() {
        return ecp;
    }

    public void setEcp(String ecp) {
        this.ecp = ecp;
    }

    public String getEcp_fix() {
        return ecp_fix;
    }

    public void setEcp_fix(String ecp_fix) {
        this.ecp_fix = ecp_fix;
    }

    public String getEccol() {
        return eccol;
    }

    public void setEccol(String eccol) {
        this.eccol = eccol;
    }

    public String getDiv() {
        return div;
    }

    public void setDiv(String div) {
        this.div = div;
    }

    public String getYld() {
        return yld;
    }

    public void setYld(String yld) {
        this.yld = yld;
    }

    public String getEo() {
        return eo;
    }

    public void setEo(String eo) {
        this.eo = eo;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getHi() {
        return hi;
    }

    public void setHi(String hi) {
        this.hi = hi;
    }

    public String getLo() {
        return lo;
    }

    public void setLo(String lo) {
        this.lo = lo;
    }

    public String getVo() {
        return vo;
    }

    public void setVo(String vo) {
        if (vo.contentEquals("-")) {
           this.vo = "0";
        } else this.vo = vo;
    }

    public String getAvvo() {
        return avvo;
    }

    public void setAvvo(String avvo) {
        this.avvo = avvo;
    }

    public String getHi52() {
        return hi52;
    }

    public void setHi52(String hi52) {
        this.hi52 = hi52;
    }

    public String getLo52() {
        return lo52;
    }

    public void setLo52(String lo52) {
        this.lo52 = lo52;
    }

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public String getPe() {
        return pe;
    }

    public void setPe(String pe) {
        this.pe = pe;
    }

    public String getFwpe() {
        return fwpe;
    }

    public void setFwpe(String fwpe) {
        this.fwpe = fwpe;
    }

    public String getBeta() {
        return beta;
    }

    public void setBeta(String beta) {
        this.beta = beta;
    }

    public String getEps() {
        return eps;
    }

    public void setEps(String eps) {
        this.eps = eps;
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public String getInst_own() {
        return inst_own;
    }

    public void setInst_own(String inst_own) {
        this.inst_own = inst_own;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        // Name Modification to standardize Bloomberg with Google and Local DB
        // Remove , from name
        // Have all Corp be Corporation
        String str = name.replaceAll(",",""); 
        String str2 = str.replaceAll("Corp[.]", "Corporation");
        String str3 = str2.replaceAll("Inc[.]", "Inc");
        
        this.name = str3;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}



/*
http://qsb-mac-plugins.googlecode.com/svn-history/r4/trunk/stock-quoter/trunk/StockQuoter.py

The Google Finance feed can return some or all of the following
keys:

  avvo    * Average volume (float with multiplier, like '3.54M')
  beta    * Beta (float)
  c       * Amount of change while open (float)
  ccol    * (unknown) (chars)
  cl        Last perc. change
  cp      * Change perc. while open (float)
  e       * Exchange (text, like 'NASDAQ')
  ec      * After hours last change from close (float)
  eccol   * (unknown) (chars)
  ecp     * After hours last chage perc. from close (float)
  el      * After. hours last quote (float)
  el_cur  * (unknown) (float)
  elt       After hours last quote time (unknown)
  eo      * Exchange Open (0 or 1)
  eps     * Earnings per share (float)
  fwpe      Forward PE ratio (float)
  hi      * Price high (float)
  hi52    * 52 weeks high (float)
  id      * Company id (identifying number)
  l       * Last value while open (float)
  l_cur   * Last value at close (like 'l')
  lo      * Price low (float)
  lo52    * 52 weeks low (float)
  lt        Last value date/time
  ltt       Last trade time (Same as "lt" without the data)
  mc      * Market cap. (float with multiplier, like '123.45B')
  name    * Company name (text)
  op      * Open price (float)
  pe      * PE ratio (float)
  t       * Ticker (text)
  type    * Type (i.e. 'Company')
  vo      * Volume (float with multiplier, like '3.54M')

  * - Provided in the feed.
*/

/* "id": "13756934"
,"t" : ".IXIC"
,"e" : "INDEXNASDAQ"
,"l" : "4,526.06"
,"l_fix" : "4526.06"
,"l_cur" : "4,526.06"
,"s": "0"
,"ltt":"5:15PM EST"
,"lt" : "Jan 13, 5:15PM EST"
,"lt_dts" : "2016-01-13T17:15:59Z"
,"c" : "-159.85"
,"c_fix" : "-159.85"
,"cp" : "-3.41"
,"cp_fix" : "-3.41"
,"ccol" : "chr"
,"pcls_fix" : "4685.9189"
,"eo" : ""
,"delay": ""
,"op" : "4,706.02"
,"hi" : "4,713.98"
,"lo" : "4,517.56"
,"vo" : "2.50B"
,"avvo" : ""
,"hi52" : "5,231.94"
,"lo52" : "4,292.14"
,"mc" : ""
,"pe" : ""
,"fwpe" : ""
,"beta" : ""
,"eps" : ""
,"shares" : ""
,"inst_own" : ""
,"name" : "NASDAQ Composite"
,"type" : "Company"

*/


/*
Apple stock example

// [
{
"id": "22144"
,"t" : "AAPL"
,"e" : "NASDAQ"
,"l" : "101.42"
,"l_fix" : "101.42"
,"l_cur" : "101.42"
,"s": "0"
,"ltt":"4:00PM EST"
,"lt" : "Jan 22, 4:00PM EST"
,"lt_dts" : "2016-01-22T16:00:01Z"
,"c" : "+5.12"
,"c_fix" : "5.12"
,"cp" : "5.32"
,"cp_fix" : "5.32"
,"ccol" : "chg"
,"pcls_fix" : "96.3"
,"eo" : ""
,"delay": ""
,"op" : "98.63"
,"hi" : "101.46"
,"lo" : "98.37"
,"vo" : "-"
,"avvo" : "-"
,"hi52" : "134.54"
,"lo52" : "92.00"
,"mc" : "562.32B"
,"pe" : "11.03"
,"fwpe" : ""
,"beta" : "0.94"
,"eps" : "9.20"
,"shares" : "5.58B"
,"inst_own" : "60%"
,"name" : "Apple Inc."
,"type" : "Company"
}
]

*/