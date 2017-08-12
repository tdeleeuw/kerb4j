/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.jaaslounge.decoding.crypto.cksum;

import org.jaaslounge.decoding.crypto.KrbException;

/**
 * Ref. MIT Krb5: krb5_hash_provider
 */

/**
 * Hash provider that provides hash function
 * for implementing a checksum type defined by Kerberos RFC3961.
 */
public interface HashProvider {

    int hashSize();
    int blockSize();

    void hash(byte[] data, int start, int size) throws KrbException;
    void hash(byte[] data) throws KrbException;
    byte[] output();
}
