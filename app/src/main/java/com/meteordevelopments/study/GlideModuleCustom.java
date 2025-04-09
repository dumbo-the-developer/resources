package com.meteordevelopments.study;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.signature.ObjectKey;

import java.io.IOException;
import java.io.InputStream;

@GlideModule
public class GlideModuleCustom extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context,
                                   @NonNull Glide glide,
                                   @NonNull Registry registry) {
        registry.replace(String.class, InputStream.class, new AssetLoaderFactory(context));
    }

    static class AssetLoaderFactory implements ModelLoaderFactory<String, InputStream> {
        private final Context context;

        AssetLoaderFactory(Context context) {
            this.context = context.getApplicationContext();
        }

        @NonNull
        @Override
        public ModelLoader<String, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new ModelLoader<String, InputStream>() {
                @Override
                public boolean handles(@NonNull String model) {
                    return model.startsWith("file:///android_asset/");
                }

                @Override
                public LoadData<InputStream> buildLoadData(@NonNull String model,
                                                           int width,
                                                           int height,
                                                           @NonNull Options options) {
                    return new LoadData<>(new ObjectKey(model), new AssetDataFetcher(context, model));
                }
            };
        }

        @Override
        public void teardown() {
        }
    }

    static class AssetDataFetcher implements DataFetcher<InputStream> {
        private final Context context;
        private final String assetPath;
        private InputStream stream;

        AssetDataFetcher(Context context, String model) {
            this.context = context;
            this.assetPath = model.replace("file:///android_asset/", "");
        }

        @Override
        public void loadData(@NonNull Priority priority,
                             @NonNull DataCallback<? super InputStream> callback) {
            try {
                AssetManager assetManager = context.getAssets();
                stream = assetManager.open(assetPath);
                callback.onDataReady(stream);
            } catch (IOException e) {
                callback.onLoadFailed(e);
            }
        }

        @Override
        public void cleanup() {
            try {
                if (stream != null) stream.close();
            } catch (IOException ignored) {}
        }

        @Override
        public void cancel() {
        }

        @NonNull
        @Override
        public Class<InputStream> getDataClass() {
            return InputStream.class;
        }

        @NonNull
        @Override
        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }
}
