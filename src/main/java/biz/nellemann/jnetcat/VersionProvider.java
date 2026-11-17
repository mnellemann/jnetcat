/*
   Copyright 2024 mark.nellemann@gmail.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package biz.nellemann.jnetcat;

import picocli.CommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

class VersionProvider implements CommandLine.IVersionProvider {

    private static final String UNKNOWN_VERSION = "unknown";


    @Override
    public String[] getVersion() {
        return new String[] {
                "${COMMAND-FULL-NAME} " + readBuildVersion() + " (on ${os.name} ${os.version} ${os.arch})",
                "JVM: ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})" };
    }


    private String readBuildVersion() {
        try (InputStream manifestStream = VersionProvider.class.getResourceAsStream("/META-INF/MANIFEST.MF")) {
            if (manifestStream == null) {
                return UNKNOWN_VERSION;
            }
            Manifest manifest = new Manifest(manifestStream);
            Attributes attrs = manifest.getMainAttributes();
            if (attrs == null) {
                return UNKNOWN_VERSION;
            }
            String version = attrs.getValue("Build-Version");
            if (version == null || version.isEmpty()) {
                return UNKNOWN_VERSION;
            }
            return version;
        } catch (IOException e) {
            return UNKNOWN_VERSION;
        }
    }

}
