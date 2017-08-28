package de.gerdiproject.json.fao;

import java.util.List;

/**
 * This class represents a generic FaoSTAT JSON response.
 * @author Robin Weiss
 *
 * @param <T> the type of data, carried by the response
 */
public class FaoJson <T>
{
    private FaoResponseMetadata metadata;
    private List<T> data;


    public FaoResponseMetadata getMetadata()
    {
        return metadata;
    }

    public void setMetadata(FaoResponseMetadata metadata)
    {
        this.metadata = metadata;
    }

    public List<T> getData()
    {
        return data;
    }

    public void setData(List<T> data)
    {
        this.data = data;
    }
}
