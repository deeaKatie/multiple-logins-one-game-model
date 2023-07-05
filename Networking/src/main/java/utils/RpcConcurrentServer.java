package utils;

import rpcprotocol.ClientRpcReflectionWorker;
import services.IServices;

import java.net.Socket;

public class RpcConcurrentServer extends AbstractConcurrentServer{

    private IServices service;

    public RpcConcurrentServer(int port, IServices service) {
        super(port);
        this.service = service;
    }

    @Override
    protected Thread createWorker(Socket client) {
        System.out.println(client);
        ClientRpcReflectionWorker worker = new ClientRpcReflectionWorker(this.service, client);
        Thread threadWorker = new Thread(worker);
        return threadWorker;
    }
}
