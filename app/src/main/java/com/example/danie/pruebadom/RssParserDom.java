package com.example.danie.pruebadom;

/**
 * Created by danie on 29-08-2016.
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RssParserDom {
    private URL rssUrl;

    //recibe como parametro la url del documento xml
    public RssParserDom(String url)
    {
        try
        {
            this.rssUrl = new URL(url);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<Noticia> parse()
    {
        //Instanciamos la fábrica para DOM
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        List<Noticia> noticias = new ArrayList<Noticia>();

        try
        {
            DocumentBuilder builder = factory.newDocumentBuilder(); //Creamos un nuevo parser DOM

            Document dom = builder.parse(this.getInputStream());//Realizamos lalectura completa del XML
                                                                //automaticamente genera una estructura de arbol

            Element root = dom.getDocumentElement(); //el nodo principal el cual engloba a los otros

            NodeList items = root.getElementsByTagName("item");//obtenemos todos los elementos hijos de item
                                                               //en forma de lista

            for (int i=0; i<items.getLength(); i++)
            {
                Noticia noticia = new Noticia();

                Node item = items.item(i);//Obtenemos el nodo de noticia actual
                NodeList datosNoticia = item.getChildNodes();//pasamos a una lista los elementos de item

                //asiganamos a los atributos de la clase Noticia lo obtenido por la variable etiqueta
                for (int j=0; j<datosNoticia.getLength(); j++)
                {
                    //obtiene los datos de la etiqueta item
                    Node dato = datosNoticia.item(j);
                    String etiqueta = dato.getNodeName();

                    if (etiqueta.equals("title"))
                    {
                        String texto = obtenerTexto(dato);

                        noticia.setTitulo(texto);
                    }
                    else if (etiqueta.equals("pubDate"))
                    {
                        noticia.setFecha(dato.getFirstChild().getNodeValue());
                    }
                }

                noticias.add(noticia);
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return noticias;
    }
    //devuelve los datos obtenidos de la etiqueta
    private String obtenerTexto(Node dato)
    {
        StringBuilder texto = new StringBuilder();
        NodeList fragmentos = dato.getChildNodes();

        for (int k=0;k<fragmentos.getLength();k++)
        {
            texto.append(fragmentos.item(k).getNodeValue());//genera una cadena de caracteres
        }

        return texto.toString();
    }

    //se conecta con la url especifica
    private InputStream getInputStream()
    {
        try
        {
            return rssUrl.openConnection().getInputStream(); // abre coneccion y obtiene el stream
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
