// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.4 by WSRD Tencent.
// Generated from `/home/corey/samba_dir/zx.jce'
// **********************************************************************

package com.iory.zhixun.jce;

public final class ReqGetNewsContent extends com.qq.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "zhi_xun.ReqGetNewsContent";
    }

    public String fullClassName()
    {
        return "zhi_xun.ReqGetNewsContent";
    }

    public int newsId = 0;

    public int getNewsId()
    {
        return newsId;
    }

    public void  setNewsId(int newsId)
    {
        this.newsId = newsId;
    }

    public ReqGetNewsContent()
    {
    }

    public ReqGetNewsContent(int newsId)
    {
        this.newsId = newsId;
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        ReqGetNewsContent t = (ReqGetNewsContent) o;
        return (
            com.qq.taf.jce.JceUtil.equals(newsId, t.newsId) );
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
        _os.write(newsId, 0);
    }


    public void readFrom(com.qq.taf.jce.JceInputStream _is)
    {
        this.newsId = (int) _is.read(newsId, 0, true);
    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.qq.taf.jce.JceDisplayer _ds = new com.qq.taf.jce.JceDisplayer(_os, _level);
        _ds.display(newsId, "newsId");
    }

}

