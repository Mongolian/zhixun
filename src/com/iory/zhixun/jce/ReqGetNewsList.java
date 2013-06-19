// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.4 by WSRD Tencent.
// Generated from `/home/corey/samba_dir/zx.jce'
// **********************************************************************

package com.iory.zhixun.jce;

public final class ReqGetNewsList extends com.qq.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "zhi_xun.ReqGetNewsList";
    }

    public String fullClassName()
    {
        return "zhi_xun.ReqGetNewsList";
    }

    public int uin = 0;

    public String lastNewsDate = "";

    public int getUin()
    {
        return uin;
    }

    public void  setUin(int uin)
    {
        this.uin = uin;
    }

    public String getLastNewsDate()
    {
        return lastNewsDate;
    }

    public void  setLastNewsDate(String lastNewsDate)
    {
        this.lastNewsDate = lastNewsDate;
    }

    public ReqGetNewsList()
    {
    }

    public ReqGetNewsList(int uin, String lastNewsDate)
    {
        this.uin = uin;
        this.lastNewsDate = lastNewsDate;
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        ReqGetNewsList t = (ReqGetNewsList) o;
        return (
            com.qq.taf.jce.JceUtil.equals(uin, t.uin) && 
            com.qq.taf.jce.JceUtil.equals(lastNewsDate, t.lastNewsDate) );
    }

    public int hashCode()
    {
        try
        {
            throw new Exception("Need define key first!");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return 0;
    }
    public java.lang.Object clone()
    {
        java.lang.Object o = null;
        try
        {
            o = super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return o;
    }

    public void writeTo(com.qq.taf.jce.JceOutputStream _os)
    {
        _os.write(uin, 0);
        _os.write(lastNewsDate, 1);
    }


    public void readFrom(com.qq.taf.jce.JceInputStream _is)
    {
        this.uin = (int) _is.read(uin, 0, true);
        this.lastNewsDate =  _is.readString(1, true);
    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.qq.taf.jce.JceDisplayer _ds = new com.qq.taf.jce.JceDisplayer(_os, _level);
        _ds.display(uin, "uin");
        _ds.display(lastNewsDate, "lastNewsDate");
    }

}

