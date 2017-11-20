/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Check if Zipfile exists and is not corrupted
 * Take from http://stackoverflow.com/questions/2085637/how-to-check-if-a-generated-zip-file-is-corrupted
 * 
 * @author atlantis
 */
public class ZipFileUtils {
    
    
    public boolean isValid(File file) {
        ZipFile zipfile = null;
        ZipInputStream zis = null;
        try {
            zipfile = new ZipFile(file);
            zis = new ZipInputStream(new FileInputStream(file));
            ZipEntry ze = zis.getNextEntry();
            if(ze == null) {
                return false;
            }
            while(ze != null) {
                // if it throws an exception fetching any of the following then we know the file is corrupted.
                zipfile.getInputStream(ze);
                ze.getCrc();
                ze.getCompressedSize();
                ze.getName();
                ze = zis.getNextEntry();
            } 
            return true;
        } catch (ZipException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (zipfile != null) {
                    zipfile.close();
                    zipfile = null;
                }
            } catch (IOException e) {
                return false;
            } try {
                if (zis != null) {
                    zis.close();
                    zis = null;
                }
            } catch (IOException e) {
                return false;
            }
        }
}    
    
}
